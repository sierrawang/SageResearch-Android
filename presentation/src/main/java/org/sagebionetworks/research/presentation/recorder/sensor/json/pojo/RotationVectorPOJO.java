package org.sagebionetworks.research.presentation.recorder.sensor.json.pojo;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public interface RotationVectorPOJO extends CoordinatePOJO {
    String SENSOR_DATA_SUBTYPE_KEY = "sensorAndroidType";
    String ROTATION_REFERENCE_COORDINATE_KEY = "referenceCoordinate";
    String W_KEY = "w";
    String ACCURACY_KEY = "estimatedAccuracy";

    @SerializedName(SENSOR_DATA_SUBTYPE_KEY)
    @Nullable
    String getSensorDataSubtype();

    @SerializedName(ROTATION_REFERENCE_COORDINATE_KEY)
    @Nullable
    String getRotationReferenceCoordinate();

    @SerializedName(W_KEY)
    @Nullable
    Float getW();

    @SerializedName(ACCURACY_KEY)
    @Nullable
    Float getEstimatedAccuracy();
}
