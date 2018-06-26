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

package org.sagebionetworks.research.mobile_ui.recorder.device_motion;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;

import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.sagebionetworks.research.mobile_ui.recorder.DataRecorder;
import org.sagebionetworks.research.mobile_ui.recorder.Recorder;
import org.sagebionetworks.research.mobile_ui.recorder.RecorderBase;
import org.sagebionetworks.research.presentation.recorder.DeviceMotionRecorderConfigPresentation;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A DeviceMotionRecorder recorders a set of the device's motion sensors and provides access to a Single<FileResult>
 * which can access the data file once the recorder is complete.
 */
public abstract class DeviceMotionRecorder extends RecorderBase {
    private static final long MICRO_SECONDS_PER_SEC = 1000000L;

    protected final double frequency;
    protected final DeviceMotionSensors deviceMotionSensors;
    protected final Set<Integer> sensorTypes;
    protected final DataRecorder dataRecorder;
    protected Disposable disposable;

    public DeviceMotionRecorder(DeviceMotionRecorderConfigPresentation config, Context context,
            DataRecorder dataRecorder) {
        super(config.getIdentifier(), config.getStartStepIdentifier(), config.getStopStepIdentifier());
        this.frequency = config.getFrequency();
        this.sensorTypes = config.getRecorderTypes();
        int sensorDelay = this.isManualFrequency() ? this.calculateDelayBetweenSamplesInMicroSeconds()
                : SensorManager.SENSOR_DELAY_FASTEST;
        this.dataRecorder = dataRecorder;
        this.deviceMotionSensors = new DeviceMotionSensors(context, this.sensorTypes, sensorDelay);
    }

    /**
     * Converts a ReactiveSensorEvent into a String that can be recorded by the DataRecorder into a file.
     * @param event The sensor event to convert into a string.
     * @return The string conversion of the given ReactiveSensorEvent.
     */
    @NonNull
    protected abstract String getDataString(@NonNull ReactiveSensorEvent event);

    @Override
    public void start() {
        super.start();
        Flowable.create(this.deviceMotionSensors, BackpressureStrategy.BUFFER)
                .map(this::getDataString)
                .subscribeOn(Schedulers.io())
                .subscribe(this.dataRecorder);
    }


    @Override
    public void stop() {
        super.stop();
        this.deviceMotionSensors.complete();
    }

    @Override
    public void cancel() {
        super.cancel();
        this.deviceMotionSensors.cancel();
        this.dataRecorder.onError(new Throwable("Recorder canceled"));
    }

    public Single<FileResult> getFileResult() {
        return Single.create(this.dataRecorder);
    }

    /**
     * Calculates the delay between sensor samples in microseconds based on the frequency of this recorder.
     * @return the delay between sensor samples in microseconds.
     */
    protected int calculateDelayBetweenSamplesInMicroSeconds() {
        return (int)((float)MICRO_SECONDS_PER_SEC / this.frequency);
    }

    /**
     * @return true if sensor frequency does not exist, and callbacks will be based on an event, like Step Detection
     *         false if the sensor frequency will come back at a desired frequency
     */
    protected boolean isManualFrequency() {
        return this.frequency < 0;
    }
}
