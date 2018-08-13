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

package org.sagebionetworks.research.presentation.recorder.device_motion;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;

import org.sagebionetworks.research.presentation.recorder.OutputDirectoryUtil;
import org.sagebionetworks.research.presentation.recorder.data.DataLogger;
import org.sagebionetworks.research.presentation.recorder.DeviceMotionRecorderConfigPresentation;

import java.io.IOException;
import java.util.UUID;

/**
 * DeviceMotionJsonRecorder recorders the devices motion as Json into a file. It provides no summary result.
 */
public class DeviceMotionJsonRecorder extends DeviceMotionRecorder {
    public static final String JSON_FILE_START = "[";

    public static final String JSON_FILE_END = "]";

    public static final String JSON_OBJECT_DELIMINATOR = ",";

    public DeviceMotionJsonRecorder(
            final DeviceMotionRecorderConfigPresentation config, final Context context, final UUID taskUUID) throws IOException {
        super(config, context,
                new DataLogger(config.getIdentifier(), OutputDirectoryUtil
                        .getRecorderOutputDirectoryFile(taskUUID, config.getIdentifier()),
                JSON_FILE_START, JSON_FILE_END, JSON_OBJECT_DELIMINATOR));
    }

    @NonNull
    @Override
    protected String getDataString(@NonNull final ReactiveSensorEvent event) {
        return DeviceMotionJsonAdapter.createJsonObject(event).toString();
    }
}
