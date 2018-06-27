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

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import org.sagebionetworks.research.domain.recorder.RecorderConfig;
import org.sagebionetworks.research.mobile_ui.recorder.data.DataLogger;

import java.io.IOException;

/**
 * Recorders the user's distance travelled an location in Json to a file, and provides access later in the application
 * to the FileResult, Summary, and Current State of this recorder.
 */
public class DistanceJsonRecorder extends DistanceRecorder {
    public static final String JSON_FILE_START = "[";
    public static final String JSON_FILE_END = "]";
    public static final String JSON_OBJECT_DELIMINATOR = ",";

    protected boolean usesRelativeCoordinates;

    public DistanceJsonRecorder(final RecorderConfig config, final Context context) throws IOException {
        super(config, context, new DataLogger(config.getIdentifier(), null,
                JSON_FILE_START, JSON_FILE_END, JSON_OBJECT_DELIMINATOR));
        // TODO rkolmos 06/26/2018 initalize these from config.
        this.usesRelativeCoordinates = false;
    }

    @NonNull
    @Override
    protected String getDataString(@NonNull final Location event) {
        // TODO rkolmos 06/26/2018 potentially refactor start time and first location.
        CurrentDistanceInfo currentState = this.getCurrentState();
        if (currentState == null || currentState.getFirstLocation() == null) {
            throw new IllegalStateException("Cannot get data string while not recording.");
        }

        Location firstLocation = currentState.getFirstLocation();
        JsonObject jsonObject = DistanceJsonAdapter.getJsonObject(event, this.usesRelativeCoordinates,
                firstLocation);
        return jsonObject.toString();
    }
}
