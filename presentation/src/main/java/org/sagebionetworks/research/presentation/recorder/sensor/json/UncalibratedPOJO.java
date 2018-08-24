package org.sagebionetworks.research.presentation.recorder.sensor.json;

import com.google.gson.annotations.SerializedName;

public interface UncalibratedPOJO extends SensorEventPOJO {
    String X_UNCALIBRATED_KEY = "xUncalibrated";
    String Y_UNCALIBRATED_KEY = "yUncalibrated";
    String Z_UNCALIBRATED_KEY = "zUncalibrated";
    String X_BIAS_KEY = "xBias";
    String Y_BIAS_KEY = "yBias";
    String Z_BIAS_KEY = "zBias";

    @SerializedName(X_UNCALIBRATED_KEY)
    float getX();

    @SerializedName(Y_UNCALIBRATED_KEY)
    float getY();

    @SerializedName(Z_UNCALIBRATED_KEY)
    float getZ();

    @SerializedName(X_BIAS_KEY)
    float getXBias();

    @SerializedName(Y_BIAS_KEY)
    float getYBias();

    @SerializedName(Z_BIAS_KEY)
    float getZBias();
}
