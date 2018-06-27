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

package org.sagebionetworks.research.mobile_ui.recorder.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import org.sagebionetworks.research.domain.recorder.RecorderType;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.mobile_ui.recorder.Recorder;
import org.sagebionetworks.research.mobile_ui.recorder.service.RecorderService.RecorderBinder;
import org.sagebionetworks.research.presentation.model.interfaces.StepView.NavDirection;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A RecorderManager handles the work of creating recorders, and making the appropriate RecorderService calls
 * to start, stop, and cancel those recorders at the appropriate times.
 */
public class RecorderManager implements ServiceConnection {
    /**
     * Invariant:
     * bound == true exactly when binder != null && service != null.
     * binder == null exactly when service == null.
     */
    private boolean bound;
    private RecorderBinder binder;
    private RecorderService service;

    private Context context;
    private Task task;
    private UUID taskRunUUID;

    public RecorderManager(Task task, Context context) {
        this.context = context;
        this.task = task;
        Intent bindIntent = new Intent(context, RecorderService.class);
        this.bound = this.context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        this.binder = (RecorderBinder)iBinder;
        this.service = this.binder.getService();
        this.bound = true;
        // TODO rkolmos 06/20/2018 make sure the correct recorders are created.
        Set<RecorderInfo> recorders = new HashSet<>();
        Map<String, Recorder> activeRecorders = this.getActiveRecorders();
        for (RecorderInfo info : recorders) {
            if (!activeRecorders.containsKey(info.id)) {
                this.service.createRecorder(this.taskRunUUID, info.id, info.type);
            }
        }
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
        this.binder = null;
        this.service = null;
        this.bound = false;
    }

    /**
     * Starts, stops, and cancels the appropriate recorders in response to the step transition from previousStep
     * to nextStep in navDirection.
     * @param previousStep The step that has just been transitioned away from, null indicates that nextStep is the first
     *                     step.
     * @param nextStep The step that has just been transition to, null indicates that previousStep is the last step.
     * @param navDirection The direction in which the transition from previousStep to nextStep occurred in.
     */
    public void onStepTransition(@Nullable Step previousStep, @Nullable Step nextStep, @NavDirection int navDirection) {
        // TODO: 06/18/2018 rkolmos get the equivalent information from the task.
        Set<RecorderInfo> recorderInfos = new HashSet<>();

        Set<RecorderInfo> shouldStart = new HashSet<>();
        Set<RecorderInfo> shouldStop = new HashSet<>();
        Set<RecorderInfo> shouldCancel = new HashSet<>();

        for (RecorderInfo info : recorderInfos) {
            String startStepIdentifier = info.startStepId;
            String stopStepIdentifier = info.stopStepId;
            if (previousStep == null) {
                if (startStepIdentifier == null) {
                    // The task has just started so the recorder should be started if it has a null startStepIdentifier.
                    shouldStart.add(info);
                }
            } else if (nextStep == null) {
                // The task has just finished so the recorder should be stopped.
                shouldStop.add(info);
            } else if (navDirection == NavDirection.SHIFT_LEFT) {
                String nextStepIdentifier = nextStep.getIdentifier();
                if (startStepIdentifier.equals(nextStepIdentifier)) {
                    // The recorder should be started.
                    shouldStart.add(info);
                } else if (stopStepIdentifier != null && stopStepIdentifier.equals(stopStepIdentifier)) {
                    // The recorder should be stopped.
                    shouldStop.add(info);
                }
            } else if (navDirection == NavDirection.SHIFT_RIGHT) {
                // TODO: rkolmos 06/14/2018 Figure out what should happen to recorder when the user goes back.
            }
        }

        if (this.bound) {
            for (RecorderInfo info : shouldStart) {
                this.service.startRecorder(this.taskRunUUID, info.id, info.type);
            }

            for (RecorderInfo info : shouldStop) {
                this.service.stopRecorder(this.taskRunUUID, info.id);
            }

            for (RecorderInfo info : shouldCancel) {
                this.service.cancelRecorder(this.taskRunUUID, info.id);
            }
        } else {
            // TODO: rkolmos 06/20/2018 handle the service being unbound
        }
    }

    /**
     * Returns a map of Recorder Id to Recorder containing all of the recorders that are currently active. An active
     * recorder is any recorder that has been created and has not has stop() called on it.
     * @return A map of Recorder Id to Recorder containing all of the active recorders.
     */
    public ImmutableMap<String, Recorder> getActiveRecorders() {
        return this.service.getActiveRecorders(this.taskRunUUID);
    }

    /**
     * TODO rkolmos 06/18/2018 remove this class and get this info from the task.
     */
    private static final class RecorderInfo {
        String startStepId;
        String stopStepId;
        String id;
        @RecorderType
        String type;
    }
}
