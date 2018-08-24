package org.sagebionetworks.research.presentation.recorder.sensor.json.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public interface SensorEventPOJO {
    String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    String UPTIME_IN_SECONDS_KEY = "uptime";
    String TIMESTAMP_DATE_KEY = "timestampDate";
    String SENSOR_DATA_TYPE_KEY = "sensorType";
    String SENSOR_EVENT_ACCURACY_KEY = "eventAccuracy";

    @SerializedName(TIMESTAMP_DATE_KEY)
    @NonNull
    String getTimestampDate();

    @SerializedName(TIMESTAMP_IN_SECONDS_KEY)
    double getTimestampInSeconds();

    @SerializedName(UPTIME_IN_SECONDS_KEY)
    @NonNull
    double getUptimeInSeconds();

    @SerializedName(SENSOR_DATA_TYPE_KEY)
    @NonNull
    String getSensorType();

    @SerializedName(SENSOR_EVENT_ACCURACY_KEY)
    int getAccuracy();
}
