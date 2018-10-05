package org.sagebionetworks.research.presentation.recorder.sensor.json;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SensorEventPOJOImpl implements SensorEventPOJO {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract SensorEventPOJOImpl build();

        @NonNull
        public abstract Builder setTimestampDate(@NonNull String timestampDate);

        @NonNull
        public abstract Builder setTimestampInSeconds(double timestampInSeconds);

        @NonNull
        public abstract Builder setUptimeInSeconds(double uptimeInSeconds);

        @NonNull
        public abstract Builder setSensorType(@NonNull String sensorType);

        @NonNull
        public abstract Builder setAccuracy(int accuracy);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_SensorEventPOJOImpl.Builder();
    }
}
