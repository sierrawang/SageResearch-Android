package org.sagebionetworks.research.presentation.recorder.sensor.json.pojo;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class CoordinatePOJOImpl implements CoordinatePOJO {
    @AutoValue.Builder
    public static abstract class Builder {
        public abstract CoordinatePOJOImpl build();

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
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_CoordinatePOJOImpl.Builder();
    }

    @NonNull
    public static TypeAdapter<CoordinatePOJOImpl> typeAdapter(Gson gson) {
        return new AutoValue_CoordinatePOJOImpl.GsonTypeAdapter(gson);
    }
}
