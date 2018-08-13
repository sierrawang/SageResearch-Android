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

package org.sagebionetworks.research.presentation.inject;

import android.content.Context;

import com.google.gson.Gson;

import org.sagebionetworks.research.domain.async.RecorderType;
import org.sagebionetworks.research.presentation.recorder.Recorder;
import org.sagebionetworks.research.presentation.recorder.device_motion.DeviceMotionJsonRecorder;
import org.sagebionetworks.research.presentation.recorder.distance.json.DistanceJsonRecorder;
import org.sagebionetworks.research.presentation.recorder.DeviceMotionRecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.DistanceRecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public abstract class RecorderModule {
    public interface RecorderFactory {
        Recorder create(RecorderConfigPresentation recorderConfiguration, UUID taskUUID) throws IOException;
    }

    @Provides
    @IntoMap
    @StringKey(RecorderType.MOTION)
    static RecorderFactory provideMotionJsonRecorderFactory(Context context) {
        return (recorderConfiguration, taskUUID) -> {
            if (!(recorderConfiguration instanceof DeviceMotionRecorderConfigPresentation)) {
                throw new IllegalArgumentException("RecorderConfigPresentation " + recorderConfiguration
                        + " is not a DeviceMotionRecorderConfigPresentation.");
            }

            return new DeviceMotionJsonRecorder((DeviceMotionRecorderConfigPresentation)recorderConfiguration, context, taskUUID);
        };
    }

    @Provides
    @IntoMap
    @StringKey(RecorderType.DISTANCE)
    static RecorderFactory provideDistanceJsonRecorderFactory(Context context, Gson gson) {
        return (recorderConfiguration, taskUUID) -> {
            if (!(recorderConfiguration instanceof DistanceRecorderConfigPresentation)) {
                throw new IllegalArgumentException("RecorderConfigPresentation " + recorderConfiguration
                        + " is not a DistanceRecorderConfigPresentation.");
            }

            return new DistanceJsonRecorder((DistanceRecorderConfigPresentation)recorderConfiguration, context, gson, taskUUID);
        };
    }

    @Provides
    static RecorderFactory provideRecorderFactory(final Map<String, RecorderFactory> recorderFactories) {
        return (recorderConfiguration, taskUUID) -> {
            String recorderType = recorderConfiguration.getType();
            if (!recorderFactories.containsKey(recorderType)) {
                throw new IllegalArgumentException("No recorder factory for recorder type " + recorderType);
            }

            return recorderFactories.get(recorderType).create(recorderConfiguration, taskUUID);
        };
    }
}
