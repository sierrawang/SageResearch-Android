package org.sagebionetworks.research.presentation.recorder.sensor.json;


import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class UncalibratedPOJOImpl implements UncalibratedPOJO {
    @AutoValue.Builder
    public static abstract class Builder {
        public abstract UncalibratedPOJOImpl build();

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

        @NonNull
        public abstract Builder setX(float x);

        @NonNull
        public abstract Builder setY(float y);

        @NonNull
        public abstract Builder setZ(float z);

        @NonNull
        public abstract Builder setXBias(float xBias);

        @NonNull
        public abstract Builder setYBias(float yBias);

        @NonNull
        public abstract Builder setZBias(float zBias);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_UncalibratedPOJOImpl.Builder();
    }

    @NonNull
    public static TypeAdapter<UncalibratedPOJOImpl> typeAdapter(Gson gson) {
        return new AutoValue_UncalibratedPOJOImpl.GsonTypeAdapter(gson);
    }

}
