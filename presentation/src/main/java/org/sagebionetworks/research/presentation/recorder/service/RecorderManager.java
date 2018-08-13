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

package org.sagebionetworks.research.presentation.recorder.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.presentation.inject.RecorderModule.RecorderFactory;
import org.sagebionetworks.research.presentation.recorder.Recorder;
import org.sagebionetworks.research.presentation.recorder.service.RecorderService.RecorderBinder;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationModule.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.model.interfaces.StepView.NavDirection;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;

import java.io.IOException;
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
    private RecorderFactory recorderFactory;
    private RecorderConfigPresentationFactory recorderConfigPresentationFactory;
    private Set<RecorderConfigPresentation> recorderConfigs;

    public RecorderManager(Task task, Context context, RecorderFactory recorderFactory,
            RecorderConfigPresentationFactory recorderConfigPresentationFactory) {
        this.context = context;
        this.recorderFactory = recorderFactory;
        this.recorderConfigPresentationFactory = recorderConfigPresentationFactory;
        this.task = task;
        Intent bindIntent = new Intent(context, RecorderService.class);
        this.bound = this.context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        this.recorderConfigs = this.getRecorderConfigs();
    }

    private Set<RecorderConfigPresentation> getRecorderConfigs() {
        Set<RecorderConfigPresentation> recorderConfigs = new HashSet<>();
        for (AsyncActionConfiguration asyncAction : this.task.getAsyncActions()) {
            if (asyncAction instanceof RecorderConfiguration) {
                recorderConfigs.add(recorderConfigPresentationFactory.create((RecorderConfiguration)asyncAction));
            }
        }

        return recorderConfigs;
    }

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        this.binder = (RecorderBinder)iBinder;
        this.service = this.binder.getService();
        this.bound = true;
        this.service.setRecorderFactory(this.recorderFactory);
        Map<String, Recorder> activeRecorders = this.getActiveRecorders();
        try {
            for (RecorderConfigPresentation config : this.recorderConfigs) {
                if (!activeRecorders.containsKey(config.getIdentifier())) {
                    this.service.createRecorder(this.taskRunUUID, config);
                }
            }
        } catch (IOException e) {
            // TODO rkolmos 8/13/2018 handle the IOException.
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
        Set<RecorderConfigPresentation> shouldStart = new HashSet<>();
        Set<RecorderConfigPresentation> shouldStop = new HashSet<>();
        Set<RecorderConfigPresentation> shouldCancel = new HashSet<>();

        for (RecorderConfigPresentation config : this.recorderConfigs) {
            String startStepIdentifier = config.getStartStepIdentifier();
            String stopStepIdentifier = config.getStopStepIdentifier();
            if (previousStep == null) {
                if (startStepIdentifier == null) {
                    // The task has just started so the recorder should be started if it has a null startStepIdentifier.
                    shouldStart.add(config);
                }
            } else if (nextStep == null) {
                // The task has just finished so the recorder should be stopped.
                shouldStop.add(config);
            } else if (navDirection == NavDirection.SHIFT_LEFT) {
                String nextStepIdentifier = nextStep.getIdentifier();
                String previousStepIdentifier = previousStep.getIdentifier();
                if (startStepIdentifier != null && startStepIdentifier.equals(nextStepIdentifier)) {
                    // The recorder should be started since we are navigating to it's start step.
                    shouldStart.add(config);
                } else if (stopStepIdentifier != null && stopStepIdentifier.equals(previousStepIdentifier)) {
                    // The recorder should be stopped. Since it's stop step identifier has just ended.
                    shouldStop.add(config);
                }
            } else if (navDirection == NavDirection.SHIFT_RIGHT) {
                // TODO: rkolmos 06/14/2018 Figure out what should happen to recorder when the user goes back.
            }
        }

        if (this.bound) {
            for (RecorderConfigPresentation config : shouldStart) {
                this.service.startRecorder(this.taskRunUUID, config.getIdentifier());
            }

            for (RecorderConfigPresentation config : shouldStop) {
                this.service.stopRecorder(this.taskRunUUID, config.getIdentifier());
            }

            for (RecorderConfigPresentation config : shouldCancel) {
                this.service.cancelRecorder(this.taskRunUUID, config.getIdentifier());
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
}
