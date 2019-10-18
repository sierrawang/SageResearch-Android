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

package org.sagebionetworks.research.presentation.recorder.location.json;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Helper class for ensuring coordinates get serialized with the correct key names.
 */
@AutoValue
public abstract class Coordinates {
    public static final String LONGITUDE_KEY = "longitude";
    public static final String LATITUDE_KEY = "latitude";
    public static final String RELATIVE_LATITUDE_KEY = "relativeLatitude";
    public static final String RELATIVE_LONGITUDE_KEY = "relativeLongitude";

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Coordinates build();

        public abstract Builder setLongitude(@Nullable Double longitude);

        public abstract Builder setLatitude(@Nullable Double latitude);

        public abstract Builder setRelativeLongitude(@Nullable Double relativeLongitude);

        public abstract Builder setRelativeLatitude(@Nullable Double relativeLatitude);
    }

    private static Builder builder() {
        return new AutoValue_Coordinates.Builder();
    }

    public static TypeAdapter<Coordinates> typeAdapter(Gson gson) {
        return new AutoValue_Coordinates.GsonTypeAdapter(gson);
    }

    public static Coordinates create(double longitude, double latitude, boolean usesRelativeCoordinates) {
        if (usesRelativeCoordinates) {
            return builder().setRelativeLongitude(longitude).setRelativeLatitude(latitude).build();
        } else {
            return builder().setLongitude(longitude).setLatitude(latitude).build();
        }
    }

    @SerializedName(LONGITUDE_KEY)
    @Nullable
    public abstract Double getLongitude();

    @SerializedName(LATITUDE_KEY)
    @Nullable
    public abstract Double getLatitude();

    @SerializedName(RELATIVE_LONGITUDE_KEY)
    @Nullable
    public abstract Double getRelativeLongitude();

    @SerializedName(RELATIVE_LATITUDE_KEY)
    @Nullable
    public abstract Double getRelativeLatitude();
}
