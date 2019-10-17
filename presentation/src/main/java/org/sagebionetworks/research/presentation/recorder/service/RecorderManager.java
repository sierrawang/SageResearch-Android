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

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.navigation.NavDirection;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.perform_task.TaskResultManager;
import org.sagebionetworks.research.presentation.perform_task.TaskResultManager.TaskResultManagerConnection;
import org.sagebionetworks.research.presentation.recorder.Recorder;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;
import org.sagebionetworks.research.presentation.recorder.RestartableRecorderConfiguration;
import org.sagebionetworks.research.presentation.recorder.service.RecorderService.RecorderBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

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
        Set<RecorderConfigPresentation> shouldCancel = new HashSet<>();
        Set<RecorderConfigPresentation> shouldStop = new HashSet<>();

        // There are a few scenarios I can think of that

        for (RecorderConfigPresentation config : this.recorderConfigs) {
            String startStepIdentifier = config.getStartStepIdentifier();
            String stopStepIdentifier = config.getStopStepIdentifier();

            // No matter the navigation direction, if we are leaving the stop step, we should stop the recorder
            if (previousStep != null && stopStepIdentifier.equals(previousStep.getIdentifier())) {
                LOGGER.info("previousStep is stopStep, stopping recorder config " + config.getIdentifier());
                // The recorder should be stopped. Since it's stop step identifier has just ended.
                shouldStop.add(config);
            }

            if (nextStep != null && startStepIdentifier != null &&
                    startStepIdentifier.equals(nextStep.getIdentifier())) {
                LOGGER.info("nextStep is startStep, starting recorder config " + config.getIdentifier());
                // The recorder should be started since we are navigating to it's start step.
                shouldStart.add(config);
            }

            // Did user navigate backwards?
            if (navDirection == NavDirection.SHIFT_RIGHT) {
                // There may be more scenarios we encounter as we add more complex navigation for recorders;
                // however, for now let's make sure that if we navigate backwards from the start step,
                // the recorder knows it should be stopped, because the previous step started it.
                if (previousStep != null && startStepIdentifier != null &&
                        startStepIdentifier.equals(previousStep.getIdentifier())) {
                    shouldStop.add(config);
                }
            }
        }

        // recorders configured to cancel, or to both start and stop. let's not stop or start them
        Set<RecorderConfigPresentation> startAndStopOrCancel = Sets
                .union(Sets.intersection(shouldStart, shouldStop), shouldCancel);

        if (this.bound) {
            Map<String, Recorder<? extends Result>> activeRecorders = this.getActiveRecorders();
            for (RecorderConfigPresentation config : Sets.difference(shouldStart, startAndStopOrCancel)) {
                Recorder<? extends Result> activeRecorder = activeRecorders.get(config.getIdentifier());
                // This is important to call before creating the result because this may
                // re-create the recorder to prep for a proper recorder restart
                activeRecorder = validateRecorderStateBeforeStart(activeRecorder, config);

                if (activeRecorder != null) {
                    // Only wait for results of recorders which were started
                    taskResultManagerConnectionSingle.blockingGet()
                            .addAsyncActionResult(activeRecorder.getResult());
                    this.service.startRecorder(this.taskRunUUID, config.getIdentifier());
                    LOGGER.info("Starting recorder " + config.getIdentifier());
                } else {
                    // Recorder data will not be collected and uploaded here, but at least the app will not crash
                    LOGGER.error("Failed to restart recorder " + config.getIdentifier());
                }
            }

            for (RecorderConfigPresentation config : Sets.difference(shouldStop, startAndStopOrCancel)) {
                String identifier = config.getIdentifier();
                if (activeRecorders.containsKey(identifier)) {
                    if (activeRecorders.get(identifier).isRecording()) {
                        this.service.stopRecorder(this.taskRunUUID, identifier);
                        LOGGER.info("Stopping recorder " + config.getIdentifier());
                    }
                }
            }

            for (RecorderConfigPresentation config : shouldCancel) {
                String identifier = config.getIdentifier();
                if (activeRecorders.containsKey(identifier)) {
                    if (activeRecorders.get(identifier).isRecording()) {
                        LOGGER.info("Canceling recorder " + config.getIdentifier());
                        this.service.cancelRecorder(this.taskRunUUID, identifier);
                    }
                }
            }
        } else {
            LOGGER.warn("OnStepTransition was called but RecorderService was unbound.");
            // TODO: rkolmos 06/20/2018 handle the service being unbound
        }
    }

    /**
     * Validate the state of the recorder so we know it is ok to start it without having any restart complications.
     * @param recorder we will be starting
     * @param config the config of the recorder that will be starting
     */
    @Nullable
    private Recorder<? extends Result> validateRecorderStateBeforeStart(
            @Nullable Recorder<? extends Result> recorder, @NonNull RecorderConfigPresentation config) {

        if (recorder == null) {
            // A null active recorder here means that the recorder has already run and been completed
            if (config instanceof RestartableRecorderConfiguration) {
                RestartableRecorderConfiguration restartableConfig = (RestartableRecorderConfiguration)config;
                if (!restartableConfig.getShouldDeletePrevious()) {
                    // To support Recorder restart with appending data functionality,
                    // We will need to somehow pass in a flag to ReactiveFileResultRecorder to
                    // signal it to open the outputFile for appending.
                    throw new IllegalStateException("RecorderManager cannot restart this recorder " +
                            "because getShouldDeletePrevious returns false and recorder appending is not supported yet.");
                }

                // At this point, we know that the dev has configured the recorder properly to restart
                // and they are ok with the recorder file being replaced by the new one.
                // In this case, let's re-create the recorder to allow it to restart appropriately.
                try {
                    LOGGER.info("Recreating restartable recorder " + config.getIdentifier());
                    recorder = this.service.createRecorder(this.taskRunUUID, config);
                } catch (IOException e) {
                    LOGGER.error("Encountered IOException while initializing recorder " + config.getIdentifier(), e);
                }
            } else {
                throw new IllegalStateException("RecorderManager cannot restart a recorder unless it\'s " +
                        "configured as a RestartableRecorderConfiguration and specifies " +
                        "it should delete the previous data file when restarting");
            }
        }
        return recorder;
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
