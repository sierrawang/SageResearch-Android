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

package org.sagebionetworks.research.mobile_ui.recorder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.domain.async.AsyncAction;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.presentation.model.interfaces.StepView.NavDirection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A RecorderManager handles the work of creating recorders, and making the appropriate RecorderService calls
 * to start, stop, and cancel those recorders at the appropriate times.
 */
public class RecorderManager {
    private RecorderService service;
    private Map<String, Recorder> recordersById;
    private Context context;

    public RecorderManager(RecorderService service, Task task, Context context) {
        this.service = service;
        this.context = context;
        this.recordersById = new HashMap<>();
        this.initializeRecordersFromTask(task);
    }

    /**
     * Initializes all the recorders that will be needed during the task.
     * @param task The task to initialize the recorders from.
     */
    private void initializeRecordersFromTask(Task task) {
        for (AsyncAction action : task.getAsyncActions()) {
            String identifier = action.getIdentifier();
            if (RecorderManager.isRecorder(identifier)) {
                Recorder recorder = RecorderManager.createRecorder(identifier, getRecorderTypeFromId(identifier));
                this.recordersById.put(identifier, recorder);
            }
        }
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
        Set<Recorder> shouldStart = new HashSet<>();
        Set<Recorder> shouldStop = new HashSet<>();
        Set<Recorder> shouldCancel = new HashSet<>();

        for (Recorder recorder : this.recordersById.values()) {
            String startStepIdentifier = recorder.getStartStepIdentifier();
            String stopStepIdentifier = recorder.getStopStepIdentifier();
            if (previousStep == null) {
                if (startStepIdentifier == null) {
                    // The task has just started so the recorder should be started if it has a null startStepIdentifier.
                    shouldStart.add(recorder);
                }
            } else if (nextStep == null) {
                // The task has just finished so the recorder should be stopped.
                shouldStop.add(recorder);
            } else if (navDirection == NavDirection.SHIFT_LEFT) {
                String nextStepIdentifier = nextStep.getIdentifier();
                if (startStepIdentifier.equals(nextStepIdentifier)) {
                    // The recorder should be started.
                    shouldStart.add(recorder);
                } else if (stopStepIdentifier != null && stopStepIdentifier.equals(stopStepIdentifier)) {
                    // The recorder should be stopped.
                    shouldStop.add(recorder);
                }
            } else if (navDirection == NavDirection.SHIFT_RIGHT) {
                // TODO: rkolmos 06/14/2018 Figure out what should happen to recorder when the user goes back.
            }
        }

        this.sendServiceRequests(shouldStart, RecorderActionType.START);
        this.sendServiceRequests(shouldStop, RecorderActionType.STOP);
        this.sendServiceRequests(shouldCancel, RecorderActionType.CANCEL);
    }

    /**
     * Sends a request to perform the given actionType, for each recorder in the given set.
     * @param recorders The set of recorders to perform the given actionType for.
     * @param actionType The type of action to perform on each recorder.
     */
    private void sendServiceRequests(Set<Recorder> recorders, @RecorderActionType String actionType) {
        for (Recorder recorder : recorders) {
            Intent intent = RecorderManager.createRecorderIntent(recorder, actionType);
            this.context.startService(intent);
        }
    }

    /**
     * Creates and returns an Intent that can be given to the RecorderService to perform the given actionType
     * on the given recorder.
     * @param recorder The recorder to perform the given actionType on.
     * @param actionType The type of action to perform on the given recorder.
     * @return an Intent that can be given to the RecorderService to perform the given actionType on the given
     *         recorder.
     */
    public static Intent createRecorderIntent(Recorder recorder, @RecorderActionType String actionType) {
        Intent intent = new Intent(RecorderService.class.getName());
        intent.putExtra(RecorderService.RECORDER_KEY, recorder);
        intent.putExtra(RecorderService.ACTION_KEY, actionType);
        return intent;
    }

    /**
     * Creates and returns a Recorder with the given identifier, and the given type.
     * @param identifier The identifier of the recorder to create.
     * @param type The type of recorder to create.
     * @return a Recorder with the given identifier, and the given type.
     */
    private static Recorder createRecorder(String identifier, String type) {
        // TODO rkolmos 06/14/2018 call the recorder factory here.
        Recorder result = new Recorder() {
            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }

            @Nullable
            @Override
            public String getStartStepIdentifier() {
                return null;
            }

            @Nullable
            @Override
            public String getStopStepIdentifier() {
                return null;
            }
        };

        return result;
    }

    private static boolean isRecorder(String Identifier) {
        return false;
    }

    private static String getRecorderTypeFromId(String identifier) {
        return "";
    }
}
