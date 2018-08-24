package org.sagebionetworks.research.presentation.recorder.sensor.json.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class RotationVectorPOJOImpl implements RotationVectorPOJO {
    @AutoValue.Builder
    public static abstract class Builder {
        public abstract RotationVectorPOJOImpl build();

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
        public abstract Builder setW(@Nullable Float w);

        @NonNull
        public abstract Builder setEstimatedAccuracy(@Nullable Float estimatedAccuracy);

        @NonNull
        public abstract Builder setSensorDataSubtype(@Nullable String sensorDataSubtype);

        @NonNull
        public abstract Builder setRotationReferenceCoordinate(@Nullable String rotationReferenceCoordinates);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_RotationVectorPOJOImpl.Builder();
    }

    @NonNull
    public static TypeAdapter<RotationVectorPOJOImpl> typeAdapter(Gson gson) {
        return new AutoValue_RotationVectorPOJOImpl.GsonTypeAdapter(gson);
    }
}
