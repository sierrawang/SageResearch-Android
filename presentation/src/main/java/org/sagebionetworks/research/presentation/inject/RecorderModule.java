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
import android.hardware.SensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;
import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import io.reactivex.Flowable;
import org.sagebionetworks.research.domain.async.RecorderType;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.presentation.recorder.Recorder;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.location.DistanceRecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.location.Path;
import org.sagebionetworks.research.presentation.recorder.location.PathAccumulator;
import org.sagebionetworks.research.presentation.recorder.reactive.ReactiveFileResultRecorder;
import org.sagebionetworks.research.presentation.recorder.reactive.source.ReactiveLocationFactory;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorSourceFactory;
import org.sagebionetworks.research.presentation.recorder.reactive.source.SensorSourceFactory.SensorConfig;
import org.sagebionetworks.research.presentation.recorder.sensor.DeviceMotionUtil;
import org.sagebionetworks.research.presentation.recorder.sensor.SensorRecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.util.TaskOutputFileUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.sagebionetworks.research.presentation.recorder.reactive.ReactiveFileResultRecorder.createJsonArrayLogger;

@Module
public abstract class RecorderModule {
    @Provides
    @IntoMap
    @StringKey(RecorderType.DISTANCE)
    static RecorderFactory provideDistanceJsonRecorderFactory(ReactiveLocationFactory reactiveLocationFactory,
                                                              Context context, Gson gson) {
        return (recorderConfiguration, taskUUID) -> {
            if (!(recorderConfiguration instanceof DistanceRecorderConfigPresentation)) {
                throw new IllegalArgumentException("RecorderConfigPresentation " + recorderConfiguration
                        + " is not a DistanceRecorderConfigPresentation.");
            }

            return createJsonArrayLogger(
                    recorderConfiguration.getIdentifier(),
                    reactiveLocationFactory.getLocation().scan(Path.ZERO, new PathAccumulator()),
                    gson,
                    TaskOutputFileUtil.getTaskOutputFile(
                            taskUUID,
                            recorderConfiguration.getIdentifier() + ".json",
                            context));
        };
    }

    @Provides
    static ReactiveSensors provideReactiveSensors(Context context) {
        return new ReactiveSensors(context);
    }

    @Provides
    @IntoMap
    @StringKey(RecorderType.MOTION)
    static RecorderFactory provideMotionJsonRecorderFactory(Context context, Gson gson,
                                                            SensorSourceFactory sensorSourceFactory) {
        return (recorderConfiguration, taskUUID) -> {
            if (!(recorderConfiguration instanceof SensorRecorderConfigPresentation)) {
                throw new IllegalArgumentException("RecorderConfigPresentation " + recorderConfiguration
                        + " is not a SensorRecorderConfigPresentation.");
            }

            SensorRecorderConfigPresentation sensorRecorderConfig
                    = (SensorRecorderConfigPresentation) recorderConfiguration;

            Collection<Flowable<SensorEvent>> sensorEventFlowables = new HashSet<>();
            for (SensorConfig sensorConfig : sensorRecorderConfig.getSensorConfigs()) {
                sensorEventFlowables.add(sensorSourceFactory.getSensorEvents(sensorConfig));
            }

            AtomicReference<Double> firstEventUptimeReference = new AtomicReference<>();

            // cache so first sensor event gets repeated with concat below
            Flowable<SensorEvent> allSensorsFlowable = Flowable.merge(sensorEventFlowables).cache();

            Flowable<DeviceMotionUtil.SensorEventPOJO> sensorEventPOJOFlowable =
                    Flowable.concat(allSensorsFlowable.take(1), allSensorsFlowable)
                            .map(e -> {
                                if (firstEventUptimeReference.get() == null) {
                                    // determine uptime reference and log full info about sensor
                                    DeviceMotionUtil.SensorEventPOJO first = new DeviceMotionUtil.SensorEventPOJO(e);
                                    firstEventUptimeReference.set(first.uptime);
                                    return first;
                                } else {
                                    // normal log of event based on uptime reference
                                    return DeviceMotionUtil.SENSOR_TYPE_TO_FACTORY
                                            .get(e.sensor.getType())
                                            .apply(e, firstEventUptimeReference.get());
                                }
                            });


            return ReactiveFileResultRecorder.createJsonArrayLogger(
                    recorderConfiguration.getIdentifier(),
                    sensorEventPOJOFlowable,
                    gson,
                    TaskOutputFileUtil.getTaskOutputFile(
                            taskUUID,
                            recorderConfiguration.getIdentifier() + ".json",
                            context)
            );
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

    public interface RecorderFactory {
        Recorder<? extends Result> create(RecorderConfigPresentation recorderConfiguration, UUID taskUUID) throws IOException;
    }
}
