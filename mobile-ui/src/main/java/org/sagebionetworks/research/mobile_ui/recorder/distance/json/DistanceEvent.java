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

package org.sagebionetworks.research.mobile_ui.recorder.distance.json;

import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class DistanceEvent {
    public static final String COORDINATE_KEY  = "coordinate";
    public static final String ALTITUDE_KEY    = "altitude";
    public static final String ACCURACY_KEY    = "accuracy";
    public static final String COURSE_KEY      = "course";
    public static final String SPEED_KEY       = "speed";
    public static final String TIMESTAMP_DATE_KEY = "timestampDate";
    public static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    public static final String UPTIME_IN_SECONDS_KEY = "uptime";

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract DistanceEvent build();

        public abstract Builder setTimestampDate(@NonNull String timestampDate);

        public abstract Builder setTimestampInSeconds(double timestamp);

        public abstract Builder setUptimeInSeconds(double uptime);

        public abstract Builder setCoordinates(@NonNull Coordinates coordinates);

        public abstract Builder setAccuracy(float accuracy);

        public abstract Builder setSpeed(float speed);

        public abstract Builder setAltitude(double altitude);

        public abstract Builder setBearing(float bearing);
    }

    public static Builder builder() {
        return new AutoValue_DistanceEvent.Builder();
    }

    public static TypeAdapter<DistanceEvent> typeAdapter(Gson gson) {
        return new AutoValue_DistanceEvent.GsonTypeAdapter(gson);
    }

    public static DistanceEvent create(@NonNull Location firstLocation, @NonNull Location currentLocation,
            boolean usesRelativeCoordinates) {
        double longitude = usesRelativeCoordinates ? firstLocation.getLongitude() - currentLocation.getLongitude() :
                currentLocation.getLongitude();
        double latitude = usesRelativeCoordinates ? firstLocation.getLatitude() - currentLocation.getLatitude() :
                currentLocation.getLatitude();
        Coordinates coordinates = Coordinates.create(longitude, latitude, usesRelativeCoordinates);
        long locationNanos = getElapsedNanosSinceBootFromLocation(currentLocation);
        long startTimeNanos = getElapsedNanosSinceBootFromLocation(firstLocation);
        return DistanceEvent.builder()
                .setTimestampDate("")
                .setTimestampInSeconds((locationNanos - startTimeNanos) * 1e-9)
                .setUptimeInSeconds(locationNanos * 1e-9)
                .setCoordinates(coordinates)
                .setAccuracy(currentLocation.getAccuracy())
                .setSpeed(currentLocation.getSpeed())
                .setAltitude(currentLocation.getAltitude())
                .setBearing(currentLocation.getBearing())
                .build();
    }

    @SerializedName(TIMESTAMP_DATE_KEY)
    public abstract String getTimestampDate();

    @SerializedName(TIMESTAMP_IN_SECONDS_KEY)
    public abstract double getTimestampInSeconds();

    @SerializedName(UPTIME_IN_SECONDS_KEY)
    public abstract double getUptimeInSeconds();

    @SerializedName(COORDINATE_KEY)
    public abstract Coordinates getCoordinates();

    @SerializedName(ACCURACY_KEY)
    public abstract float getAccuracy();

    @SerializedName(SPEED_KEY)
    public abstract float getSpeed();

    @SerializedName(ALTITUDE_KEY)
    public abstract double getAltitude();

    @SerializedName(COURSE_KEY)
    public abstract float getBearing();

    // Wrapper method which encapsulates getting the elapsed realtime nanos, or falls back to elasped realtime (millis)
    // for older OS versions.
    // Package-scoped so this can be mocked for unit tests.
    static long getElapsedNanosSinceBootFromLocation(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return location.getElapsedRealtimeNanos();
        } else {
            return (long) (SystemClock.elapsedRealtime() * 1e6); // millis to nanos
        }
    }
}
