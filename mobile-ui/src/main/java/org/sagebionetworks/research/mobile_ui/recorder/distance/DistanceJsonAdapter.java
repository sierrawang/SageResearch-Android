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

package org.sagebionetworks.research.mobile_ui.recorder.distance;

import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import org.sagebionetworks.research.mobile_ui.recorder.data.FormatHelper;

import java.util.Locale;

/**
 * Helper to convert Location's measured by a DistanceRecorder into JsonObjects for writing to a file.
 */
public class DistanceJsonAdapter {
    private static final String SHARED_PREFS_KEY = "LocationRecorder";
    private static final String LAST_RECORDED_DIST_KEY = "LastRecordedTotalDistance";
    public static final String COORDINATE_KEY  = "coordinate";
    public static final String LONGITUDE_KEY   = "longitude";
    public static final String LATITUDE_KEY    = "latitude";
    public static final String ALTITUDE_KEY    = "altitude";
    public static final String ACCURACY_KEY    = "accuracy";
    public static final String COURSE_KEY      = "course";
    public static final String RELATIVE_LATITUDE_KEY = "relativeLatitude";
    public static final String RELATIVE_LONGITUDE_KEY = "relativeLongitude";
    public static final String SPEED_KEY       = "speed";
    public static final String TIMESTAMP_DATE_KEY = "timestampDate";
    public static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    public static final String UPTIME_IN_SECONDS_KEY = "uptime";

    /**
     * Returns a JsonObject that represents the given location.
     * @param location The location to represent in Json.
     * @param usesRelativeCoordinates `true` if coordinates relative to the first location should be used.
     *                               `false` if absolute longitude and latitude should be used.
     * @param firstLocation The first location this distance recorder measured. Even if absolute coordinates are being
     *                      used this shouldn't be null as the timestamp is also used.
     * @return a JsonObject that represents the given location.
     */
    @NonNull
    public static JsonObject getJsonObject(@NonNull Location location,
            boolean usesRelativeCoordinates, @NonNull Location firstLocation) {
        JsonObject jsonObject = new JsonObject();
        long locationNanos = DistanceJsonAdapter.getElapsedNanosSinceBootFromLocation(location);
        long startTimeNanos = DistanceJsonAdapter.getElapsedNanosSinceBootFromLocation(firstLocation);
        // TODO rkolmos 06/26/2018 look into using a different date format class.
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            jsonObject.addProperty(TIMESTAMP_DATE_KEY, new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601,
                    Locale.getDefault()).format(location.getTime()));
        }

        // Timestamps
        // timestamp is seconds since start of the activity (locationNanos minus startTimeNanos, divided by a billion).
        // uptime is a monotonically increasing timestamp in seconds, with any arbitrary zero. (We use
        // getElapsedRealtimeNanos(), divided by a billion.)
        jsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY, (locationNanos - startTimeNanos) * 1e-9);
        jsonObject.addProperty(UPTIME_IN_SECONDS_KEY, locationNanos * 1e-9);
        JsonObject coordinateJsonObject = DistanceJsonAdapter.getCoordinateJsonObject(location, usesRelativeCoordinates,
                firstLocation);
        jsonObject.add(COORDINATE_KEY, coordinateJsonObject);
        if (location.hasAccuracy()) {
            jsonObject.addProperty(ACCURACY_KEY, location.getAccuracy());
        }

        if (location.hasSpeed()) {
            jsonObject.addProperty(SPEED_KEY, location.getSpeed());
        }

        if (location.hasAltitude()) {
            jsonObject.addProperty(ALTITUDE_KEY, location.getAltitude());
        }

        if (location.hasBearing()) {
            jsonObject.addProperty(COURSE_KEY, location.getBearing());
        }

        return jsonObject;
    }

    /**
     * Returns a JsonObject storing the coordinates of the given location.
     * @param location The location to get a coordinate json object for.
     * @param usesRelativeCoordinates `true` if the coordinates are relative to the first location, `false` if they
     *                                are absolute longitude and latitude.
     * @param firstLocation The first location the recorder recorded.
     * @return a JsonObject storing the coordinates of the given location.
     */
    private static JsonObject getCoordinateJsonObject(Location location, boolean usesRelativeCoordinates,
            Location firstLocation) {
        JsonObject coordinateJsonObject = new JsonObject();
        // GPS coordinates
        if (usesRelativeCoordinates) {
            // Subtract from the firstLocation to get relative coordinates.
            double relativeLatitude = location.getLatitude() - firstLocation.getLatitude();
            double relativeLongitude = location.getLongitude() - firstLocation.getLongitude();
            coordinateJsonObject.addProperty(RELATIVE_LATITUDE_KEY, relativeLatitude);
            coordinateJsonObject.addProperty(RELATIVE_LONGITUDE_KEY, relativeLongitude);
        } else {
            // Use absolute gps coordinates.
            coordinateJsonObject.addProperty(LONGITUDE_KEY, location.getLongitude());
            coordinateJsonObject.addProperty(LATITUDE_KEY, location.getLatitude());
        }

        return coordinateJsonObject;
    }


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
