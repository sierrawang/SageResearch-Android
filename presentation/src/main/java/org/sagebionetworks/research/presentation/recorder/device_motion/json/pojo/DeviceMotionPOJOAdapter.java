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

package org.sagebionetworks.research.presentation.recorder.device_motion.json.pojo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import org.sagebionetworks.research.presentation.recorder.data.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Provides support for converting a ReactiveSensorEvent into a JsonObject.
 */
public class DeviceMotionPOJOAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMotionPOJOAdapter.class);

    private static long TIMESTAMP_ZERO_REFERENCE_NANOS = 0;

    private static final float GRAVITY_SI_CONVERSION = SensorManager.GRAVITY_EARTH;

    private static final Map<Integer, String> SENSOR_TYPE_TO_DATA_TYPE;

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
    }

    /**
     * Creates and returns the most basic type of SensorEventPOJO containing information about the timestamps,
     * sensorType, and accuracy.
     *
     * @param sensorEvent
     *         the sensor event to create a POJO for.
     * @return the most basic type of SensorEventPOJO containing information about the timestamps, sensorType, and
     *         accuracy.
     */
    private static SensorEventPOJO createGenericSensorEventPOJO(SensorEvent sensorEvent) {
        if (TIMESTAMP_ZERO_REFERENCE_NANOS <= 0) {
            // set timestamp reference, which timestamps are measured relative to
            TIMESTAMP_ZERO_REFERENCE_NANOS = sensorEvent.timestamp;
        }

        long uptimeNanos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            uptimeNanos = SystemClock.elapsedRealtimeNanos();
        } else {
            uptimeNanos = (long) (SystemClock.elapsedRealtime() * 1e6); // millis to nanos
        }

        long timestampReferenceMillis = System.currentTimeMillis()
                + (long) ((TIMESTAMP_ZERO_REFERENCE_NANOS - uptimeNanos) * 1e-6d);
        Date timestampReferenceDate = new Date(timestampReferenceMillis);
        String timestampDate = new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601, Locale.getDefault())
                .format(timestampReferenceDate);
        double timestampInSeconds = (sensorEvent.timestamp - TIMESTAMP_ZERO_REFERENCE_NANOS) * 1e-9;
        double uptimeInSeconds = sensorEvent.timestamp * 1e-9;
        int sensorType = sensorEvent.sensor.getType();
        String sensorTypeString = SENSOR_TYPE_TO_DATA_TYPE.get(sensorType);
        if (Strings.isNullOrEmpty(sensorTypeString)) {
            LOGGER.warn("Unable find type key for sensor type: "
                    + sensorType);
        }

        return SensorEventPOJOImpl.builder()
                .setTimestampDate(timestampDate)
                .setTimestampInSeconds(timestampInSeconds)
                .setUptimeInSeconds(uptimeInSeconds)
                .setSensorType(sensorTypeString)
                .setAccuracy(sensorEvent.accuracy)
                .build();
    }

    /**
     * Creates and returns a coordinate POJO containing all the same information as a SensorEventPOJO with the
     * addition ofx, y, and z coordinates.
     *
     * @param sensorEvent
     *         the sensor event to create a POJO for.
     * @param gravitySIConversion
     *         The gravity conversion to divide the x, y, and z coordinates by.
     * @return A coordinate POJO containing all the same information as a SensorEventPOJO with the addition of x, y,
     *         and z coordinates.
     */
    private static CoordinatePOJO createCoordinatePOJO(SensorEvent sensorEvent, float gravitySIConversion) {
        SensorEventPOJO sensorEventPOJO = DeviceMotionPOJOAdapter.createGenericSensorEventPOJO(sensorEvent);
        return CoordinatePOJOImpl.builder()
                .setTimestampDate(sensorEventPOJO.getTimestampDate())
                .setTimestampInSeconds(sensorEventPOJO.getTimestampInSeconds())
                .setUptimeInSeconds(sensorEventPOJO.getUptimeInSeconds())
                .setSensorType(sensorEventPOJO.getSensorType())
                .setAccuracy(sensorEventPOJO.getAccuracy())
                .setX(sensorEvent.values[0] / gravitySIConversion)
                .setY(sensorEvent.values[1] / gravitySIConversion)
                .setZ(sensorEvent.values[2] / gravitySIConversion)
                .build();
    }

    /**
     * Creates and returns an uncalibrated POJO containing all the same information as a SensorEventPOJO with the
     * addition of x, y, and z coordinates and biases.
     *
     * @param sensorEvent
     *         The sensor event to create a POJO for.
     * @return an uncalibrated POJO containing all the same information as a SensorEventPOJO with the addition of x,
     *         y, and z coordinates and biases.
     */
    private static UncalibratedPOJO createUncalibratedPOJO(SensorEvent sensorEvent) {
        SensorEventPOJO sensorEventPOJO = DeviceMotionPOJOAdapter.createGenericSensorEventPOJO(sensorEvent);
        return UncalibratedPOJOImpl.builder()
                .setTimestampDate(sensorEventPOJO.getTimestampDate())
                .setTimestampInSeconds(sensorEventPOJO.getTimestampInSeconds())
                .setUptimeInSeconds(sensorEventPOJO.getUptimeInSeconds())
                .setSensorType(sensorEventPOJO.getSensorType())
                .setAccuracy(sensorEventPOJO.getAccuracy())
                .setX(sensorEvent.values[0])
                .setY(sensorEvent.values[1])
                .setZ(sensorEvent.values[2])
                .setXBias(sensorEvent.values[3])
                .setYBias(sensorEvent.values[4])
                .setZBias(sensorEvent.values[5])
                .build();
    }

    /**
     * Creates a rotation vector POJO with all the same information as a CoordinatePOJO with the possible addition of
     * a sensor data subtype, a rotation reference coordinate, a w coordinate, and an estimated accuracy.
     *
     * @param sensorEvent
     *         the Sensor event to create a POJO for.
     * @return a rotation vector POJO with all the same information as a CoordinatePOJO with the possible addition of
     *         a sensor data subtype, a rotation reference coordinate, a w coordinate, and an estimated accuracy.
     */
    private static RotationVectorPOJO createRotationVectorPOJO(SensorEvent sensorEvent) {
        SensorEventPOJO sensorEventPOJO = DeviceMotionPOJOAdapter.createGenericSensorEventPOJO(sensorEvent);
        // Figure out the sennsorDataSubtype, and rotationReferenceCoordinate
        String sensorDataSubtype = null;
        String rotationReferenceCoordinate = null;
        int sensorType = sensorEvent.sensor.getType();
        if (Sensor.TYPE_ROTATION_VECTOR == sensorType) {
            sensorDataSubtype = "rotationVector";
            rotationReferenceCoordinate = "East-Up-North";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && Sensor.TYPE_GAME_ROTATION_VECTOR == sensorType) {
            sensorDataSubtype = "gameRotationVector";
            rotationReferenceCoordinate = "zUp";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR == sensorType) {
            sensorDataSubtype = "geomagneticRotationVector";
            rotationReferenceCoordinate = "East-Up-North";
        }

        Float w = null;
        Float estimatedAccuracy = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            w = sensorEvent.values[3];
            // game rotation vector never provides accuracy, always returns zero
            if (Sensor.TYPE_GAME_ROTATION_VECTOR != sensorType) {
                // estimated accuracy in radians, or -1 if unavailable
                estimatedAccuracy = sensorEvent.values[4];
            }
        } else if (sensorEvent.values.length > 3) {
            w = sensorEvent.values[3];
        }

        return RotationVectorPOJOImpl.builder()
                .setTimestampDate(sensorEventPOJO.getTimestampDate())
                .setTimestampInSeconds(sensorEventPOJO.getTimestampInSeconds())
                .setUptimeInSeconds(sensorEventPOJO.getUptimeInSeconds())
                .setSensorType(sensorEventPOJO.getSensorType())
                .setAccuracy(sensorEventPOJO.getAccuracy())
                .setX(sensorEvent.values[0])
                .setY(sensorEvent.values[1])
                .setZ(sensorEvent.values[2])
                .setSensorDataSubtype(sensorDataSubtype)
                .setRotationReferenceCoordinate(rotationReferenceCoordinate)
                .setW(w)
                .setEstimatedAccuracy(estimatedAccuracy)
                .build();
    }

    /**
     * Returns a POJO for the given sensorEvent. The returned POJO is read to be written to a result file.
     *
     * @param sensorEvent
     *         the sensor event to create a POJO for.
     * @return a POJO for the given sensorEvent.
     */
    @Nullable
    public static SensorEventPOJO createSensorEventPOJO(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor != null) {
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                case Sensor.TYPE_LINEAR_ACCELERATION:
                case Sensor.TYPE_GRAVITY:
                    return DeviceMotionPOJOAdapter.createCoordinatePOJO(sensorEvent, GRAVITY_SI_CONVERSION);
                case Sensor.TYPE_MAGNETIC_FIELD:
                case Sensor.TYPE_GYROSCOPE:
                    // using a gravity conversion of 1f leaves the x, y, and z coordinates unchanged.
                    return DeviceMotionPOJOAdapter.createCoordinatePOJO(sensorEvent, 1f);
                case Sensor.TYPE_ROTATION_VECTOR:
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    return DeviceMotionPOJOAdapter.createRotationVectorPOJO(sensorEvent);
                case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    return DeviceMotionPOJOAdapter.createUncalibratedPOJO(sensorEvent);
            }
        }

        LOGGER.warn("SensorEvent: " + sensorEvent + " couldn't be converted to a POJO");
        return null;
    }
}
