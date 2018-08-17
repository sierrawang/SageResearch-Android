package org.sagebionetworks.research.presentation.recorder.device_motion.json.pojo;

import com.google.gson.annotations.SerializedName;

public interface CoordinatePOJO extends SensorEventPOJO {
    String X_KEY = "x";
    String Y_KEY = "y";
    String Z_KEY = "z";

    @SerializedName(X_KEY)
    float getX();

    @SerializedName(Y_KEY)
    float getY();

    @SerializedName(Z_KEY)
    float getZ();
}
