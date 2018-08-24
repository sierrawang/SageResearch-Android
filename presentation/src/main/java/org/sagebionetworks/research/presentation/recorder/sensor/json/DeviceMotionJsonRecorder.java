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

package org.sagebionetworks.research.presentation.recorder.sensor.json;

import android.content.Context;

import com.google.gson.Gson;

import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.sagebionetworks.research.presentation.recorder.util.TaskOutputFileUtil;
import org.sagebionetworks.research.presentation.recorder.sensor.SensorRecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.reactive.ReactiveFileResultRecorder;
import org.sagebionetworks.research.presentation.recorder.sensor.DeviceMotionRecorder;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorRecorderSourceFactory;

import java.io.IOException;
import java.util.UUID;

import io.reactivex.Maybe;

/**
 * DeviceMotionRecorder recorders a set of the device's motion sensors and provides access to a Single<FileResult>
 */
public class DeviceMotionJsonRecorder extends DeviceMotionRecorder {
    private final Gson gson;

    private final ReactiveFileResultRecorder reactiveFileResultRecorder;

    public DeviceMotionJsonRecorder(final SensorRecorderConfigPresentation config, final Context context,
            final Gson gson, final UUID taskUUID, final SensorRecorderSourceFactory sensorRecorderSourceFactory)
            throws IOException {
        super(config,
                sensorRecorderSourceFactory);
        this.gson = gson;

        this.reactiveFileResultRecorder = ReactiveFileResultRecorder.createJsonArrayLogger(
                config.getIdentifier(),
                getEventConnectableFlowable(),
                gson,
                TaskOutputFileUtil.getTaskOutputFile(
                        taskUUID,
                        config.getIdentifier() + ".json",
                        context));
    }

    @Override
    public Maybe<FileResult> getResult() {
        return reactiveFileResultRecorder.getResult();
    }

    @Override
    public void startRecorder() {
        super.startRecorder();
        reactiveFileResultRecorder.startRecorder();
    }

    @Override
    public void stopRecorder() {
        super.stopRecorder();
        reactiveFileResultRecorder.stopRecorder();
    }

    @Override
    public void cancelRecorder() {
        super.cancelRecorder();
        reactiveFileResultRecorder.cancelRecorder();
    }
}
