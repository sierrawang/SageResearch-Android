package org.sagebionetworks.research.presentation.recorder.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import io.reactivex.functions.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by TheMDP on 2/5/17.
 * <p>
 * The DeviceMotionUtil incorporates a bunch of sensor fusion sensor readings
 * together to paint a broad picture of the device's orientation and movement over time.
 * <p>
 * This class is an attempt at recording data in a similar way as iOS' device motion recorder.
 *
 * @see <a href="https://developer.android.com/reference/android/hardware/SensorEvent.html#values">
 * Sensor values</a>
 * @see <a href="https://source.android.com/devices/sensors/sensor-type">Sensor Types</a>
 * @see <a href="https://developer.android.com/guide/topics/sensors/sensors_position.html">
 * Position Sensors</a>
 * @see <a href="https://developer.android.com/guide/topics/sensors/sensors_motion.html">
 * Motion Sensors</a>
 */
public final class DeviceMotionUtil {
    public static final float GRAVITY_SI_CONVERSION = SensorManager.GRAVITY_EARTH;
    public static final String SENSOR_DATA_TYPE_KEY = "sensorType";
    public static final String SENSOR_DATA_SUBTYPE_KEY = "sensorAndroidType";
    public static final String SENSOR_EVENT_ACCURACY_KEY = "eventAccuracy";
    public static final Map<Integer, String> SENSOR_TYPE_TO_DATA_TYPE;
    public static final Set<Integer> ROTATION_VECTOR_TYPES;
    public static final String ROTATION_REFERENCE_COORDINATE_KEY = "referenceCoordinate";
    public static final String X_KEY = "x";
    public static final String Y_KEY = "y";
    public static final String Z_KEY = "z";
    public static final String W_KEY = "w";
    public static final String ACCURACY_KEY = "estimatedAccuracy";
    public static final String X_UNCALIBRATED_KEY = "xUncalibrated";
    public static final String Y_UNCALIBRATED_KEY = "yUncalibrated";
    public static final String Z_UNCALIBRATED_KEY = "zUncalibrated";
    public static final String X_BIAS_KEY = "xBias";
    public static final String Y_BIAS_KEY = "yBias";
    public static final String Z_BIAS_KEY = "zBias";
    public static final Map<Integer, Class<? extends SensorEventPOJO>> SENSOR_TYPE_TO_EVENT_POJO =
            new ImmutableMap.Builder<Integer, Class<? extends SensorEventPOJO>>()
                    .put(Sensor.TYPE_ACCELEROMETER, AccelerationEventPojo.class)
                    .put(Sensor.TYPE_GRAVITY, AccelerationEventPojo.class)
                    .put(Sensor.TYPE_LINEAR_ACCELERATION, AccelerationEventPojo.class)
                    .put(Sensor.TYPE_GYROSCOPE, GyroscopeEventPOJO.class)
                    .put(Sensor.TYPE_MAGNETIC_FIELD, MagneticEventPojo.class)
                    .put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, UncalibratedEventPOJO.class)
                    .put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, UncalibratedEventPOJO.class)
                    .put(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, UncalibratedEventPOJO.class)
                    .put(Sensor.TYPE_GAME_ROTATION_VECTOR, RotationEventPojo.class)
                    .put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, RotationEventPojo.class)
                    .put(Sensor.TYPE_ROTATION_VECTOR, RotationEventPojo.class)
                    .build();
    public static final Map<Integer, BiFunction<SensorEvent, Double, ? extends SensorEventPOJO>> SENSOR_TYPE_TO_FACTORY =
            new ImmutableMap.Builder<Integer, BiFunction<SensorEvent, Double, ? extends SensorEventPOJO>>()
                    .put(Sensor.TYPE_ACCELEROMETER, AccelerationEventPojo::create)
                    .put(Sensor.TYPE_GRAVITY, AccelerationEventPojo::create)
                    .put(Sensor.TYPE_LINEAR_ACCELERATION, AccelerationEventPojo::create)
                    .put(Sensor.TYPE_GYROSCOPE, GyroscopeEventPOJO::create)
                    .put(Sensor.TYPE_MAGNETIC_FIELD, MagneticEventPojo::create)
                    .put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, UncalibratedEventPOJO::create)
                    .put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, UncalibratedEventPOJO::create)
                    .put(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, UncalibratedEventPOJO::create)
                    .put(Sensor.TYPE_GAME_ROTATION_VECTOR, RotationEventPojo::create)
                    .put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, RotationEventPojo::create)
                    .put(Sensor.TYPE_ROTATION_VECTOR, RotationEventPojo::create)
                    .build();

    private static final Logger logger = LoggerFactory.getLogger(DeviceMotionUtil.class);

    static {
        // build mapping for sensor type and its data type value
        ImmutableMap.Builder<Integer, String> sensorTypeMapBuilder = ImmutableMap.builder();
        // rotation/gyroscope
        sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE, "rotationRate");
        sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "rotationRateUncalibrated");

        // accelerometer
        sensorTypeMapBuilder.put(Sensor.TYPE_ACCELEROMETER, "acceleration");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sensorTypeMapBuilder.put(
                    Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, "accelerationUncalibrated");
        }

        // gravity
        sensorTypeMapBuilder.put(Sensor.TYPE_GRAVITY, "gravity");

        // acceleration without gravity
        sensorTypeMapBuilder.put(Sensor.TYPE_LINEAR_ACCELERATION, "userAcceleration");

        // magnetic field
        sensorTypeMapBuilder.put(Sensor.TYPE_MAGNETIC_FIELD, "magneticField");
        sensorTypeMapBuilder.put(
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "magneticFieldUncalibrated");

        // attitude
        sensorTypeMapBuilder.put(Sensor.TYPE_ROTATION_VECTOR, "attitude");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "attitude");
        }
        SENSOR_TYPE_TO_DATA_TYPE = sensorTypeMapBuilder.build();

        // build mappint for rotation type
        ImmutableSet.Builder<Integer> rotationTypeBuilder = ImmutableSet.builder();
        rotationTypeBuilder.add(Sensor.TYPE_ROTATION_VECTOR);
        rotationTypeBuilder.add(Sensor.TYPE_GAME_ROTATION_VECTOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            rotationTypeBuilder.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        }
        ROTATION_VECTOR_TYPES = rotationTypeBuilder.build();
    }

    /**
     * @param availableSensorList the list of available sensors
     * @param sensorType          the sensor type to check if it is contained in the list
     * @return true if that sensor type is available, false if it is not
     */
    static boolean hasAvailableType(List<Sensor> availableSensorList, int sensorType) {
        for (Sensor sensor : availableSensorList) {
            if (sensor.getType() == sensorType) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> getSensorTypeList(List<Sensor> availableSensorList) {
        List<Integer> sensorTypeList = new ArrayList<>();

        // Only add these sensors if the device has them
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER)) {
            sensorTypeList.add(Sensor.TYPE_ACCELEROMETER);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)) {
            sensorTypeList.add(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        }

        if (hasAvailableType(availableSensorList, Sensor.TYPE_GRAVITY)) {
            sensorTypeList.add(Sensor.TYPE_GRAVITY);
        }

        if (hasAvailableType(availableSensorList, Sensor.TYPE_LINEAR_ACCELERATION)) {
            sensorTypeList.add(Sensor.TYPE_LINEAR_ACCELERATION);
        }

        if (hasAvailableType(availableSensorList, Sensor.TYPE_GYROSCOPE)) {
            sensorTypeList.add(Sensor.TYPE_GYROSCOPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && hasAvailableType(availableSensorList, Sensor.TYPE_GYROSCOPE_UNCALIBRATED)) {
            sensorTypeList.add(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        }

        if (hasAvailableType(availableSensorList, Sensor.TYPE_MAGNETIC_FIELD)) {
            sensorTypeList.add(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && hasAvailableType(availableSensorList, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)) {
            sensorTypeList.add(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        }

        if (hasAvailableType(availableSensorList, Sensor.TYPE_ROTATION_VECTOR)) {
            sensorTypeList.add(Sensor.TYPE_ROTATION_VECTOR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (hasAvailableType(availableSensorList, Sensor.TYPE_GAME_ROTATION_VECTOR)) {
                sensorTypeList.add(Sensor.TYPE_GAME_ROTATION_VECTOR);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasAvailableType(availableSensorList, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)) {
                sensorTypeList.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            }
        }

        return sensorTypeList;
    }

    public void recordSensorEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        int sensorType = sensorEvent.sensor.getType();
        String sensorTypeKey = SENSOR_TYPE_TO_DATA_TYPE.get(sensorType);

        if (Strings.isNullOrEmpty(sensorTypeKey)) {
            logger.warn("Unable find type key for sensor type: "
                    + sensorType);
            return;
        }

        jsonObject.addProperty(SENSOR_DATA_TYPE_KEY, sensorTypeKey);
        jsonObject.addProperty(SENSOR_EVENT_ACCURACY_KEY, sensorEvent.accuracy);

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                recordAccelerometerEvent(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GRAVITY:
                recordGravityEvent(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                recordLinearAccelerometerEvent(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GYROSCOPE:
                recordGyroscope(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                recordMagneticField(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                recordUncalibrated(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_ROTATION_VECTOR:
                recordRotationVector(sensorEvent, jsonObject);
                break;
            default:
                logger.warn("Unable to record sensor type: " + sensorType);
        }
    }

    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#accelerometer">
     * Sensor Types: Accelerometer</a>
     */
    @VisibleForTesting
    void recordAccelerometerEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#linear_acceleration">
     * Sensor Types: Accelerometer</a>
     */
    @VisibleForTesting
    void recordLinearAccelerometerEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        // acceleration = gravity + linear-acceleration
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * Direction and magnitude of gravity.
     *
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#gravity">
     * Sensor Types: Gravity </a>
     */
    @VisibleForTesting
    void recordGravityEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * Sensor.TYPE_ROTATION_VECTOR relative to East-North-Up coordinate frame.
     * Sensor.TYPE_GAME_ROTATION_VECTOR  no magnetometer
     * Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR similar to a rotation vector sensor but using a
     * magnetometer and no gyroscope
     *
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#attitude_composite_sensors">
     * https://source.android.com/devices/sensors/sensor-types#rotation_vector
     * https://source.android.com/devices/sensors/sensor-types#game_rotation_vector
     * https://source.android.com/devices/sensors/sensor-types#geomagnetic_rotation_vector
     */
    @VisibleForTesting
    void recordRotationVector(SensorEvent sensorEvent, JsonObject jsonObject) {
        // indicate android sensor subtype
        int sensorType = sensorEvent.sensor.getType();
        if (Sensor.TYPE_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "rotationVector");
            jsonObject.addProperty(ROTATION_REFERENCE_COORDINATE_KEY, "East-Up-North");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && Sensor.TYPE_GAME_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "gameRotationVector");
            jsonObject.addProperty(ROTATION_REFERENCE_COORDINATE_KEY, "zUp");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "geomagneticRotationVector");
            jsonObject.addProperty(ROTATION_REFERENCE_COORDINATE_KEY, "East-Up-North");
        }

        // x = rot_axis.y * sin(theta/2)
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        // y = rot_axis.y * sin(theta/2)
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        // z = rot_axis.z * sin(theta/2)
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // w = cos(theta/2)
            jsonObject.addProperty(W_KEY, sensorEvent.values[3]);

            // game rotation vector never provides accuracy, always returns zero
            if (Sensor.TYPE_GAME_ROTATION_VECTOR != sensorType) {
                // estimated accuracy in radians, or -1 if unavailable
                jsonObject.addProperty(ACCURACY_KEY, sensorEvent.values[4]);
            }
        } else if (sensorEvent.values.length > 3) {
            // this value was optional before SDK Level 18
            // w = cos(theta/2)
            jsonObject.addProperty(W_KEY, sensorEvent.values[3]);
        }
    }

    @VisibleForTesting
    void recordGyroscope(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    @VisibleForTesting
        // used for uncalibrated gyroscope, uncalibrated accelerometer, and uncalibrated magnetic field
    void recordUncalibrated(SensorEvent sensorEvent, JsonObject jsonObject) {
        // conceptually: _uncalibrated = _calibrated + _bias.
        jsonObject.addProperty(X_UNCALIBRATED_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_UNCALIBRATED_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_UNCALIBRATED_KEY, sensorEvent.values[2]);

        jsonObject.addProperty(X_BIAS_KEY, sensorEvent.values[3]);
        jsonObject.addProperty(Y_BIAS_KEY, sensorEvent.values[4]);
        jsonObject.addProperty(Z_BIAS_KEY, sensorEvent.values[5]);
    }

    @VisibleForTesting
    void recordMagneticField(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    public static class SensorEventPOJO {
        private static int SECONDS_TO_NANOS = 1_000_000_000;
        // ISO-8601 timestamp for the uptime zero
        @Nullable
        public final Sensor sensor;
        @Nullable
        public final Instant timestampDate;
        public final String sensorType;
        // timestamp in seconds
        public final double timestamp;
        // uptime in seconds
        public final double uptime;
        public int eventAccuracy;

        /**
         * @param event                     sensor event
         * @param referenceTimestampSeconds uptime zero in nanos from epoch
         */
        public SensorEventPOJO(SensorEvent event, double referenceTimestampSeconds) {
            uptime = (double) event.timestamp / SECONDS_TO_NANOS;
            timestamp = uptime - referenceTimestampSeconds;
            sensorType = SENSOR_TYPE_TO_DATA_TYPE.get(event.sensor.getType());
            timestampDate = null;
            sensor = null;
        }

        // used to log initial event, contains more data about sensor and reference for subsequent events
        public SensorEventPOJO(SensorEvent event) {
            uptime = (double) event.timestamp / SECONDS_TO_NANOS;
            timestampDate = uptimeZero();
            timestamp = 0;
            sensorType = SENSOR_TYPE_TO_DATA_TYPE.get(event.sensor.getType());
            sensor = event.sensor;
        }

        public static long toNanos(@NonNull Instant timestamp) {
            return timestamp.getEpochSecond() * SECONDS_TO_NANOS + timestamp.getNano();
        }

        /**
         * @return instant of system uptime zero
         */
        @NonNull
        public static Instant uptimeZero() {
            return Instant.now().minus(SystemClock.elapsedRealtimeNanos(), ChronoUnit.NANOS);
        }

        public static Instant timestampToInstant(long timestamp) {
            // record date equivalent of timestamp reference---------------------------------------------------------------------
            return uptimeZero().plus(timestamp, ChronoUnit.NANOS);
        }
    }

    public static class AccelerationEventPojo extends SensorEventPOJO {
        public final double x;
        public final double y;
        public final double z;
        public final String unit = "g";

        public AccelerationEventPojo(SensorEvent sensorEvent, double referenceTimestampNanos) {
            super(sensorEvent, referenceTimestampNanos);
            x = sensorEvent.values[0] / SensorManager.GRAVITY_EARTH;
            y = sensorEvent.values[1] / SensorManager.GRAVITY_EARTH;
            z = sensorEvent.values[2] / SensorManager.GRAVITY_EARTH;
        }

        public static AccelerationEventPojo create(SensorEvent event, double referenceTimestampNanos) {
            return new AccelerationEventPojo(event, referenceTimestampNanos);
        }
    }

    public static class GyroscopeEventPOJO extends SensorEventPOJO {
        public final double x;
        public final double y;
        public final double z;
        public final String unit = "rad/s";

        public GyroscopeEventPOJO(SensorEvent sensorEvent, double referenceTimestampNanos) {
            super(sensorEvent, referenceTimestampNanos);
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
        }

        public static GyroscopeEventPOJO create(SensorEvent event, double referenceTimestampNanos) {
            return new GyroscopeEventPOJO(event, referenceTimestampNanos);
        }
    }

    public static class MagneticEventPojo extends SensorEventPOJO {
        public final double x;
        public final double y;
        public final double z;
        public final String unit = "uT";

        public MagneticEventPojo(SensorEvent sensorEvent, double referenceTimestampNanos) {
            super(sensorEvent, referenceTimestampNanos);
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
        }

        public static MagneticEventPojo create(SensorEvent event, double referenceTimestampNanos) {
            return new MagneticEventPojo(event, referenceTimestampNanos);
        }
    }

    public static class RotationEventPojo extends SensorEventPOJO {
        @Nullable
        public final String referenceCoordinate;
        @Nullable
        public final String sensorAndroidType;
        public final double x;
        public final double y;
        public final double z;
        public final double w;
        public double estimatedAccuracy;

        public RotationEventPojo(SensorEvent event, double referenceTimestampNanos) {
            super(event, referenceTimestampNanos);
            // rot_axis.x * sin(theta/2)
            x = event.values[0];
            // rot_axis.y * sin(theta/2)
            y = event.values[1];
            // rot_axis.z * sin(theta/2)
            z = event.values[2];
            // cos(theta/2)
            w = event.values[3];

            estimatedAccuracy = event.values[4];
            int sensorType = event.sensor.getType();
            if (Sensor.TYPE_ROTATION_VECTOR == sensorType) {
                sensorAndroidType = "rotationVector";
                referenceCoordinate = "East-Up-North";
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    && Sensor.TYPE_GAME_ROTATION_VECTOR == sensorType) {
                sensorAndroidType = "gameRotationVector";
                referenceCoordinate = "zUp";
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                    && Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR == sensorType) {
                sensorAndroidType = "geomagneticRotationVector";
                referenceCoordinate = "East-Up-North";
            } else {
                sensorAndroidType = null;
                referenceCoordinate = null;
            }
        }

        public static RotationEventPojo create(SensorEvent event, double referenceTimestampNanos) {
            return new RotationEventPojo(event, referenceTimestampNanos);
        }
    }

    public static class UncalibratedEventPOJO extends SensorEventPOJO {
        public final double xUncalibrated;
        public final double xBias;
        public final double yUncalibrated;
        public final double yBias;
        public final double zUncalibrated;
        public final double zBias;

        public UncalibratedEventPOJO(SensorEvent event, double referenceTimestampNanos) {
            super(event, referenceTimestampNanos);
            xUncalibrated = event.values[0];
            yUncalibrated = event.values[1];
            zUncalibrated = event.values[2];
            xBias = event.values[3];
            yBias = event.values[4];
            zBias = event.values[5];
        }

        public static UncalibratedEventPOJO create(SensorEvent event, double referenceTimestampNanos) {
            return new UncalibratedEventPOJO(event, referenceTimestampNanos);
        }
    }
}
