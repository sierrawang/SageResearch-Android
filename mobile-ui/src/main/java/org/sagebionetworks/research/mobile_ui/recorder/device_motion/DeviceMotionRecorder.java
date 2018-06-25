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
import org.sagebionetworks.research.presentation.recorder.DeviceMotionRecorderConfigPresentation;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public abstract class DeviceMotionRecorder implements Recorder {
    public static final String JSON_START_DELIMINATOR = "[";
    public static final String JSON_END_DELIMINATOR = "]";
    public static final String JSON_OBJECT_SEPARATOR = ",";
    private static final long MICRO_SECONDS_PER_SEC = 1000000L;

    protected final double frequency;
    protected final String identifier;
    protected final String startStepIdentifier;
    protected final String stopStepIdentifier;
    protected final DeviceMotionSensors deviceMotionSensors;
    protected final Set<Integer> sensorTypes;
    protected final DataRecorder dataRecorder;
    protected boolean isRecording;

    public DeviceMotionRecorder(DeviceMotionRecorderConfigPresentation config, Context context) throws
            IOException {
        this.frequency = config.getFrequency();
        this.identifier = config.getIdentifier();
        this.startStepIdentifier = config.getStartStepIdentifier();
        this.stopStepIdentifier = config.getStopStepIdentifier();
        this.sensorTypes = config.getRecorderTypes();
        this.deviceMotionSensors = new DeviceMotionSensors(context);
        this.dataRecorder = this.instantiateRecorder(config);
        this.isRecording = false;
    }

    /**
     * Instantiates a DataRecorder with the correct settings to log for this DeviceMotionRecorder
     * @return A DataRecorder fully configured for this DeviceMotionRecorder
     * @throws IOException if instantiating the DataRecorder fails with an IOException.
     */
    @NonNull
    public abstract DataRecorder instantiateRecorder(DeviceMotionRecorderConfigPresentation config) throws IOException;

    /**
     * Converts a ReactiveSensorEvent into a String that can be recorded by the DataRecorder into a file.
     * @param event The sensor event to convert into a string.
     * @return The string conversion of the given ReactiveSensorEvent.
     */
    @NonNull
    public abstract String getDataString(@NonNull ReactiveSensorEvent event);

    @Override
    public void start() {
        this.isRecording = true;
        int sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
        if (!this.isManualFrequency()) {
            sensorDelay = this.calculateDelayBetweenSamplesInMicroSeconds();
        }

        File outputDirectory = this.getOutputDirectory();
        this.deviceMotionSensors.subscribeToSensors(sensorTypes, sensorDelay)
                .map(this::getDataString)
                .subscribeOn(Schedulers.io())
                .subscribe(this.dataRecorder);
    }

    /**
     * Returns the file that this recorder will output to.
     * @return the file that this recorder will output to.
     */
    protected File getOutputDirectory() {
        // TODO rkolmos 06/25/2018 implement this method.
        return new File("");
    }

    @Override
    public void stop() {
        this.isRecording = false;
    }

    @Override
    public void cancel() {
        this.isRecording = false;
        this.dataRecorder.onError(new Throwable("Recorder canceled"));
    }

    @Override
    public boolean isRecording() {
        return this.isRecording;
    }

    @Nullable
    @Override
    public String getStartStepIdentifier() {
        return this.startStepIdentifier;
    }

    @Nullable
    @Override
    public String getStopStepIdentifier() {
        return this.stopStepIdentifier;
    }

    /**
     * Retruns a Single that will publish the Json file result when the file is finished being written.
     * @return a Single that will publish the Json file result when the file is finihsed being written.
     */
    public Single<FileResult> getJsonFileResult() {
        return Single.create(this.dataRecorder);
    }

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
