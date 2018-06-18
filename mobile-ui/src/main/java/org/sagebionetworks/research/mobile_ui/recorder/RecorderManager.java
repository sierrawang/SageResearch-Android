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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

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
    private Context context;
    private Task task;

    public RecorderManager(RecorderService service, Task task, Context context) {
        this.service = service;
        this.context = context;
        this.task = task;
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
        for (RecorderInfo info : recorderInfos) {
            String startStepIdentifier = info.startStepId;
            String stopStepIdentifier = info.stopStepId;
            if (previousStep == null) {
                if (startStepIdentifier == null) {
                    // The task has just started so the recorder should be started if it has a null startStepIdentifier.
                    this.context.startService(RecorderManager.createRecorderIntent(info.id, RecorderActionType.START,
                            info.type));
                }
            } else if (nextStep == null) {
                // The task has just finished so the recorder should be stopped.
                this.context.startService(RecorderManager.createRecorderIntent(info.id, RecorderActionType.STOP, null));
            } else if (navDirection == NavDirection.SHIFT_LEFT) {
                String nextStepIdentifier = nextStep.getIdentifier();
                if (startStepIdentifier.equals(nextStepIdentifier)) {
                    // The recorder should be started.
                    this.context.startService(RecorderManager.createRecorderIntent(info.id, RecorderActionType.START,
                            info.type));
                } else if (stopStepIdentifier != null && stopStepIdentifier.equals(stopStepIdentifier)) {
                    // The recorder should be stopped.
                    this.context.startService(RecorderManager.createRecorderIntent(info.id, RecorderActionType.STOP,
                            null));
                }
            } else if (navDirection == NavDirection.SHIFT_RIGHT) {
                // TODO: rkolmos 06/14/2018 Figure out what should happen to recorder when the user goes back.
            }
        }
    }

    public ImmutableMap<String, Recorder> getActiveRecorders() {
        return this.service.getActiveRecorders();
    }

    /**
     * Creates and returns an Intent that can be given to the RecorderService to perform the given actionType
     * on the given recorder.
     * @param recorderId The identifier of the recorder to perform the given actionType on.
     * @param actionType The type of action to perform on the given recorder.
     * @param recorderType The type of recorder should one need to be created, null otherwise.
     * @return an Intent that can be given to the RecorderService to perform the given actionType on the given
     *         recorder.
     */
    public static Intent createRecorderIntent(@NonNull String recorderId, @RecorderActionType String actionType,
            @Nullable @RecorderType String recorderType) {
        Intent intent = new Intent(RecorderService.class.getName());
        intent.putExtra(RecorderService.RECORDER_ACTION_KEY, actionType);
        intent.putExtra(RecorderService.RECORDER_ID_KEY, recorderId);
        if (recorderType != null && actionType.equals(RecorderActionType.START)) {
            intent.putExtra(RecorderService.RECORDER_TYPE_KEY, recorderType);
        }

        return intent;
    }

    /**
     * TODO rkolmos 06/18/2018 remove this class and get this info from the task.
     */
    private static final class RecorderInfo {
        String startStepId;
        String stopStepId;
        String id;
        @RecorderType String type;
    }
}
