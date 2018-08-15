/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.presentation.recorder.device_motion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.VisibleForTesting;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;

import org.sagebionetworks.research.presentation.recorder.data.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Provides support for converting a ReactiveSensorEvent into a JsonObject.
 */
public class DeviceMotionJsonAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMotionJsonAdapter.class);

    private static long TIMESTAMP_ZERO_REFERENCE_NANOS = 0;

    // Constants for writing to JSON
    public static final Map<Integer, String> SENSOR_TYPE_TO_DATA_TYPE;

    public static final Set<Integer> ROTATION_VECTOR_TYPES;

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

    public static final float GRAVITY_SI_CONVERSION = SensorManager.GRAVITY_EARTH;

    public static final String SENSOR_DATA_TYPE_KEY = "sensorType";

    public static final String SENSOR_DATA_SUBTYPE_KEY = "sensorAndroidType";

    public static final String SENSOR_EVENT_ACCURACY_KEY = "eventAccuracy";

    public static final String ROTATION_REFERENCE_COORDINATE_KEY = "referenceCoordinate";

    public static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";

    public static final String UPTIME_IN_SECONDS_KEY = "uptime";

    public static final String TIMESTAMP_DATE_KEY = "timestampDate";

    /**
     * Creates and returns a JsonObject which stores the information about the given ReactiveSensorEvent.
     *
     * @param event
     *         The event to create a JsonObject from.
     * @return A JsonObject storing all the information from event.
     */
    public static JsonObject createJsonObject(ReactiveSensorEvent event) {
        JsonObject jsonObject = new JsonObject();
        SensorEvent sensorEvent = event.getSensorEvent();
        DeviceMotionJsonAdapter.recordCommonElements(sensorEvent, jsonObject);
        // Record the event as the correct type of sensor event.
        Sensor sensor = sensorEvent.sensor;
        if (sensor != null) {
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    recordAccelerometerEvent(sensorEvent, jsonObject);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    recordLinearAccelerometerEvent(sensorEvent, jsonObject);
                    break;
                case Sensor.TYPE_GRAVITY:
                    recordGravityEvent(sensorEvent, jsonObject);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    recordGyroscopeEvent(sensorEvent, jsonObject);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    recordMagneticFieldEvent(sensorEvent, jsonObject);
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    recordRotationVectorEvent(sensorEvent, jsonObject);
                    break;
                case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    recordUncalibratedEvent(sensorEvent, jsonObject);
                    break;
            }
        }

        return jsonObject;
    }

    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#accelerometer"> Sensor Types:
     * Accelerometer</a>
     */
    @VisibleForTesting
    static void recordAccelerometerEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * Direction and magnitude of gravity.
     *
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#gravity"> Sensor Types: Gravity </a>
     */
    @VisibleForTesting
    static void recordGravityEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    static void recordGyroscopeEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#linear_acceleration"> Sensor Types:
     * Accelerometer</a>
     */
    @VisibleForTesting
    static void recordLinearAccelerometerEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        // acceleration = gravity + linear-acceleration
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    static void recordMagneticFieldEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    /**
     * Sensor.TYPE_ROTATION_VECTOR relative to East-North-Up coordinate frame. Sensor.TYPE_GAME_ROTATION_VECTOR  no
     * magnetometer Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR similar to a rotation vector sensor but using a
     * magnetometer and no gyroscope
     *
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#attitude_composite_sensors">
     * https://source.android.com/devices/sensors/sensor-types#rotation_vector https://source.android.com/devices/sensors/sensor-types#game_rotation_vector
     * https://source.android.com/devices/sensors/sensor-types#geomagnetic_rotation_vector
     */
    @VisibleForTesting
    static void recordRotationVectorEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
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

    // used for uncalibrated gyroscope, uncalibrated accelerometer, and uncalibrated magnetic field
    static void recordUncalibratedEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        // conceptually: _uncalibrated = _calibrated + _bias.
        jsonObject.addProperty(X_UNCALIBRATED_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_UNCALIBRATED_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_UNCALIBRATED_KEY, sensorEvent.values[2]);

        jsonObject.addProperty(X_BIAS_KEY, sensorEvent.values[3]);
        jsonObject.addProperty(Y_BIAS_KEY, sensorEvent.values[4]);
        jsonObject.addProperty(Z_BIAS_KEY, sensorEvent.values[5]);
    }

    /**
     * Records all the properties that are common across all SensorEvent's json representations.
     *
     * @param sensorEvent
     *         The SensorEvent to record the properties from.
     * @param jsonObject
     *         The json object to record the properties in.
     */
    private static void recordCommonElements(SensorEvent sensorEvent, JsonObject jsonObject) {
        if (TIMESTAMP_ZERO_REFERENCE_NANOS <= 0) {
            // set timestamp reference, which timestamps are measured relative to
            TIMESTAMP_ZERO_REFERENCE_NANOS = sensorEvent.timestamp;

            // record date equivalent of timestamp reference
            long uptimeNanos;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                uptimeNanos = SystemClock.elapsedRealtimeNanos();
            } else {
                uptimeNanos = (long) (SystemClock.elapsedRealtime() * 1e6); // millis to nanos
            }

            long timestampReferenceMillis = System.currentTimeMillis()
                    + (long) ((TIMESTAMP_ZERO_REFERENCE_NANOS - uptimeNanos) * 1e-6d);
            Date timestampReferenceDate = new Date(timestampReferenceMillis);
            jsonObject.addProperty(TIMESTAMP_DATE_KEY,
                    new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601, Locale.getDefault())
                            .format(timestampReferenceDate));
        }

        // these values are doubles
        jsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY,
                (sensorEvent.timestamp - TIMESTAMP_ZERO_REFERENCE_NANOS) * 1e-9);
        jsonObject.addProperty(UPTIME_IN_SECONDS_KEY, sensorEvent.timestamp * 1e-9);
        int sensorType = sensorEvent.sensor.getType();
        String sensorTypeKey = SENSOR_TYPE_TO_DATA_TYPE.get(sensorType);
        if (Strings.isNullOrEmpty(sensorTypeKey)) {
            LOGGER.warn("Unable find type key for sensor type: "
                    + sensorType);
        }

        jsonObject.addProperty(SENSOR_DATA_TYPE_KEY, sensorTypeKey);
        jsonObject.addProperty(SENSOR_EVENT_ACCURACY_KEY, sensorEvent.accuracy);
    }

    static {
        // build mapping for sensor type and its data type value
        ImmutableMap.Builder<Integer, String> sensorTypeMapBuilder = ImmutableMap.builder();
        // rotation/gyroscope
        sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE, "rotationRate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "rotationRateUncalibrated");
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(
                    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "magneticFieldUncalibrated");
        }

        // attitude
        sensorTypeMapBuilder.put(Sensor.TYPE_ROTATION_VECTOR, "attitude");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GAME_ROTATION_VECTOR, "attitude");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "attitude");
        }
        SENSOR_TYPE_TO_DATA_TYPE = sensorTypeMapBuilder.build();

        // build mappint for rotation type
        ImmutableSet.Builder<Integer> rotationTypeBuilder = ImmutableSet.builder();
        rotationTypeBuilder.add(Sensor.TYPE_ROTATION_VECTOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            rotationTypeBuilder.add(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            rotationTypeBuilder.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        }
        ROTATION_VECTOR_TYPES = rotationTypeBuilder.build();
    }
}
