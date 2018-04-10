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

package org.sagebionetworks.research.sdk.presentation.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import java.util.UUID;
import org.sagebionetworks.research.sdk.presentation.model.LoadableResource;
import org.sagebionetworks.research.sdk.presentation.model.StepView;
import org.sagebionetworks.research.sdk.presentation.model.StepView.NavDirection;
import org.sagebionetworks.research.sdk.presentation.model.TaskView;
import org.sagebionetworks.research.sdk.result.Result;
import org.sagebionetworks.research.sdk.result.TaskResult;
import org.sagebionetworks.research.sdk.step.Step;
import org.sagebionetworks.research.sdk.task.Task;
import org.sagebionetworks.research.sdk.task.Task.Progress;
import org.sagebionetworks.research.sdk.task.navigation.StepNavigator;
import org.sagebionetworks.research.sdk.task.navigation.StepNavigatorFactory;
import org.threeten.bp.Instant;

@MainThread
public class PerformTaskViewModel extends ViewModel {

    private final TaskView taskView;

    private final StepNavigatorFactory stepNavigatorFactory;

    private final MutableLiveData<TaskResult> taskResultLiveData;

    private final MutableLiveData<LoadableResource<Task>> taskLiveData;

    private final MutableLiveData<StepView> stepViewMutableLiveData;

    private final TaskResult.Builder taskResultBuilder;

    private StepNavigator stepNavigator;

    private final MutableLiveData<Step> currentStepLiveData;

    private final LiveData<Step> nextStepLiveData;

    private final LiveData<Step> previousStepLiveData;

    private final LiveData<Progress> progressLiveData;


    public PerformTaskViewModel(@NonNull TaskView taskView, @NonNull StepNavigatorFactory stepNavigatorFactory) {
        this.taskView = checkNotNull(taskView);
        this.stepNavigatorFactory = checkNotNull(stepNavigatorFactory);

        taskLiveData = new MutableLiveData<>();
        taskResultLiveData = new MutableLiveData<>();
        stepViewMutableLiveData = new MutableLiveData<>();
        currentStepLiveData = new MutableLiveData<>();
        taskResultBuilder = new TaskResult.Builder("id", UUID.randomUUID());
        taskResultBuilder.setStartTime(Instant.now());

        nextStepLiveData = Transformations.switchMap(currentStepLiveData, (Step s) ->
            Transformations.map(taskResultLiveData, (TaskResult tr) -> stepNavigator.getNextStep(s, tr))
        );
        previousStepLiveData = Transformations.switchMap(currentStepLiveData, (Step s) ->
            Transformations.map(taskResultLiveData, (TaskResult tr) -> stepNavigator.getPreviousStep(s, tr))
        );
        progressLiveData = Transformations.switchMap(currentStepLiveData, (Step s) ->
            Transformations.map(taskResultLiveData, (TaskResult tr) -> stepNavigator.getProgress(s, tr))
        );
    }

    @NonNull
    public TaskView getTaskView() {
        return taskView;
    }

    @NonNull
    public LiveData<LoadableResource<Task>> getTask() {
        return taskLiveData;
    }

    @NonNull
    public LiveData<StepView> getStep() {
        return stepViewMutableLiveData;
    }

    @NonNull
    public LiveData<TaskResult> getTaskResult() {
        return taskResultLiveData;
    }


    public void addStepResult(Result result) {
        taskResultBuilder.addStepResult(result);
        taskResultLiveData.setValue(taskResultBuilder.build());
    }

    public void addAsyncResult(Result result) {
        taskResultBuilder.addAsyncResult(result);
        taskResultLiveData.setValue(taskResultBuilder.build());
    }

    public void goForward() {
        Step forwardStep = nextStepLiveData.getValue();
        if (forwardStep != null) {
            stepViewMutableLiveData.setValue(new StepView(NavDirection.SHIFT_LEFT));
            currentStepLiveData.setValue(forwardStep);
        }
    }

    public void goBack() {
        Step backwardStep = previousStepLiveData.getValue();
        if (backwardStep != null) {
            stepViewMutableLiveData.setValue(new StepView(NavDirection.SHIFT_RIGHT));
            currentStepLiveData.setValue(backwardStep);
        }
    }
}
