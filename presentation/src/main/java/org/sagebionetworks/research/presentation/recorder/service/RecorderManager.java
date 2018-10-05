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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.model.interfaces.StepView.NavDirection;
import org.sagebionetworks.research.presentation.perform_task.TaskResultManager;
import org.sagebionetworks.research.presentation.perform_task.TaskResultManager.TaskResultManagerConnection;
import org.sagebionetworks.research.presentation.recorder.Recorder;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.service.RecorderService.RecorderBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A RecorderManager managers a Task's recorders.
 * <p>
 * RecorderManager creates a Task's recorders and makes the appropriate RecorderService calls to start, stop, and
 * cancel those recorders at the appropriate times.
 */
public class RecorderManager implements ServiceConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecorderManager.class);
    private final CompositeDisposable compositeDisposable;
    private final Context context;
    private final RecorderConfigPresentationFactory recorderConfigPresentationFactory;
    private final Set<RecorderConfigPresentation> recorderConfigs;
    private final Task task;
    private final Single<TaskResultManagerConnection> taskResultManagerConnectionSingle;
    private final UUID taskRunUUID;
    private RecorderBinder binder;
    /**
     * Invariant: bound == true exactly when binder != null && service != null. binder == null exactly when service ==
     * null.
     */
    private boolean bound;
    private RecorderService service;

    // TODO: a way to wait until service is bound
    // TODO: unbind service
    public RecorderManager(@NonNull Task task, @NonNull String taskIdentifier, @NonNull UUID taskRunUUID,
                           Context context,
                           @NonNull TaskResultManager taskResultManager,
                           RecorderConfigPresentationFactory recorderConfigPresentationFactory) {
        this.task = checkNotNull(task);
        this.taskRunUUID = checkNotNull(taskRunUUID);
        this.context = checkNotNull(context);

        taskResultManagerConnectionSingle = taskResultManager
                .getTaskResultManagerConnection(taskIdentifier, taskRunUUID);
        this.recorderConfigPresentationFactory = checkNotNull(recorderConfigPresentationFactory);

        this.compositeDisposable = new CompositeDisposable();
        Intent bindIntent = new Intent(context, RecorderService.class);
        this.context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        this.recorderConfigs = this.getRecorderConfigs();
    }

    /**
     * Returns a map of Recorder Id to Recorder containing all of the recorders that are currently active. An active
     * recorder is any recorder that has been created and has not had stop() called on it.
     *
     * @return A map of Recorder Id to Recorder containing all of the active recorders.
     */
    @NonNull
    public ImmutableMap<String, Recorder<? extends Result>> getActiveRecorders() {
        if (this.bound) {
            return this.service.getActiveRecorders(this.taskRunUUID);
        } else {
            LOGGER.warn("Cannot get active recorders Service is not bound");
            return ImmutableMap.of();
        }
    }

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        this.binder = (RecorderBinder) iBinder;
        this.service = this.binder.getService();
        this.bound = true;
        Map<String, Recorder<? extends Result>> activeRecorders = this.getActiveRecorders();
        try {
            for (RecorderConfigPresentation config : this.recorderConfigs) {
                if (!activeRecorders.containsKey(config.getIdentifier())) {
                    this.service.createRecorder(this.taskRunUUID, config);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Encountered IOException while initializing recorders", e);
            // TODO rkolmos 8/13/2018 handle the IOException.
        }
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
        this.binder = null;
        this.service = null;
        this.bound = false;
        this.compositeDisposable.dispose();
    }

    /**
     * Starts, stops, and cancels the appropriate recorders in response to the step transition from previousStep to
     * nextStep in navDirection.
     *
     * @param previousStep The step that has just been transitioned away from, null indicates that nextStep is the first step.
     * @param nextStep     The step that has just been transition to, null indicates that previousStep is the last step.
     * @param navDirection The direction in which the transition from previousStep to nextStep occurred in.
     */
    public void onStepTransition(@Nullable Step previousStep, @Nullable Step nextStep,
                                 @NavDirection int navDirection) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("onStepTransition called from: " + previousStep + ", to: " + nextStep + " in direction: "
                    + navDirection);
        }

        Set<RecorderConfigPresentation> shouldStart = new HashSet<>();
        Set<RecorderConfigPresentation> shouldStop = new HashSet<>();
        Set<RecorderConfigPresentation> shouldCancel = new HashSet<>();

        for (RecorderConfigPresentation config : this.recorderConfigs) {
            String startStepIdentifier = config.getStartStepIdentifier();
            String stopStepIdentifier = config.getStopStepIdentifier();
            if (navDirection == NavDirection.SHIFT_LEFT) {
                if (nextStep != null && startStepIdentifier.equals(nextStep.getIdentifier())) {
                    // The recorder should be started since we are navigating to it's start step.
                    shouldStart.add(config);
                }
                if (previousStep != null && stopStepIdentifier.equals(previousStep.getIdentifier())) {
                    // The recorder should be stopped. Since it's stop step identifier has just ended.
                    shouldStop.add(config);
                }
            } else if (navDirection == NavDirection.SHIFT_RIGHT) {
                // TODO: rkolmos 06/14/2018 Figure out what should happen to recorder when the user goes back.
            }
        }

        // recorders configured to cancel, or to both start and stop. let's not stop or start them
        Set<RecorderConfigPresentation> startAndStopOrCancel = Sets
                .union(Sets.intersection(shouldStart, shouldStop), shouldCancel);

        if (this.bound) {
            Map<String, Recorder<? extends Result>> activeRecorders = this.getActiveRecorders();
            for (RecorderConfigPresentation config : Sets.difference(shouldStart, startAndStopOrCancel)) {
                // only wait for results of recorders which were started
                taskResultManagerConnectionSingle.blockingGet()
                        .addAsyncActionResult(activeRecorders.get(config.getIdentifier()).getResult());
                this.service.startRecorder(this.taskRunUUID, config.getIdentifier());
            }

            for (RecorderConfigPresentation config : Sets.difference(shouldStop, startAndStopOrCancel)) {
                String identifier = config.getIdentifier();
                if (activeRecorders.containsKey(identifier)) {
                    if (activeRecorders.get(identifier).isRecording()) {
                        this.service.stopRecorder(this.taskRunUUID, identifier);
                    }
                }
            }

            for (RecorderConfigPresentation config : shouldCancel) {
                String identifier = config.getIdentifier();
                if (activeRecorders.containsKey(identifier)) {
                    if (activeRecorders.get(identifier).isRecording()) {
                        this.service.cancelRecorder(this.taskRunUUID, identifier);
                    }
                }
            }
        } else {
            LOGGER.warn("OnStepTransition was called but RecorderService was unbound.");
            // TODO: rkolmos 06/20/2018 handle the service being unbound
        }
    }

    private Set<RecorderConfigPresentation> getRecorderConfigs() {
        Set<RecorderConfigPresentation> recorderConfigs = new HashSet<>();
        for (AsyncActionConfiguration asyncAction : this.task.getAsyncActions()) {
            if (asyncAction instanceof RecorderConfiguration) {
                recorderConfigs.add(
                        recorderConfigPresentationFactory.create((RecorderConfiguration) asyncAction));
            }
        }

        return recorderConfigs;
    }
}
