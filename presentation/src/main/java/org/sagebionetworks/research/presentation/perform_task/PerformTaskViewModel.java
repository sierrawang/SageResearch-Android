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

package org.sagebionetworks.research.presentation.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.AnswerResultType;
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase;
import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.interfaces.ThemedUIStep;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfo;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.presentation.ActionType;
import org.sagebionetworks.research.presentation.contract.model.LoadableResource;
import org.sagebionetworks.research.presentation.inject.StepViewModule.StepViewFactory;
import org.sagebionetworks.research.presentation.mapper.TaskMapper;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

@MainThread
public class PerformTaskViewModel extends ViewModel {
    public static final String LAST_RUN_RESULT_ID = "lastRun";

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformTaskViewModel.class);

    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<Step> currentStepLiveData;

    private final ZonedDateTime lastRun;

    private StepNavigator stepNavigator;

    private final StepNavigatorFactory stepNavigatorFactory;

    private final StepViewFactory stepViewFactory;

    private final MutableLiveData<StepView> stepViewLiveData;

    private Map<Step, StepView> stepViewMapping;

    private Task task;

    private final MutableLiveData<TaskInfo> taskLiveData;

    private final TaskMapper taskMapper;

    private final MutableLiveData<TaskProgress> taskProgressLiveData;

    private final TaskRepository taskRepository;

    private final MutableLiveData<TaskResult> taskResultLiveData;

    private final UUID taskRunUuid;

    private final TaskView taskView;

    private final MutableLiveData<LoadableResource<TaskView>> taskViewLiveData;

    public PerformTaskViewModel(@NonNull TaskView taskView, @NonNull UUID taskRunUUID,
            @NonNull StepNavigatorFactory stepNavigatorFactory, @NonNull TaskRepository taskRepository,
            @NonNull TaskMapper taskMapper, StepViewFactory stepViewFactory,
            @Nullable ZonedDateTime lastRun) {
        this.taskView = checkNotNull(taskView);
        this.taskRunUuid = checkNotNull(taskRunUUID);
        this.stepNavigatorFactory = checkNotNull(stepNavigatorFactory);
        this.taskRepository = checkNotNull(taskRepository);
        this.taskMapper = checkNotNull(taskMapper);
        this.stepViewFactory = stepViewFactory;
        this.lastRun = lastRun;

        // TODO migrate these LiveData to StepNavigationViewModel @liujoshua 2018/08/07

        taskLiveData = new MutableLiveData<>();
        taskResultLiveData = new MutableLiveData<>();

        taskProgressLiveData = new MutableLiveData<>();
        taskProgressLiveData.setValue(null);

        currentStepLiveData = new MutableLiveData<>();
        currentStepLiveData.setValue(null);

        stepViewLiveData = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        taskViewLiveData = new MutableLiveData<>();

        initTaskSteps(taskView, taskRunUuid);
    }


    public void addAsyncResult(Result result) {
        checkState(taskResultLiveData.getValue() != null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addAsyncResult called with result: {}", result);
        }

        taskResultLiveData.setValue(taskResultLiveData.getValue().addAsyncResult(result));
    }

    public void addStepResult(Result result) {
        checkState(taskResultLiveData.getValue() != null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addStepResult called with result: {}", result);
        }
        taskResultLiveData.setValue(taskResultLiveData.getValue().addStepHistory(result));
    }

    /**
     * Returns the task's default ActionView for the given ActionType. The ActionView overrides the appearance of the
     * actions buttons throughout the task. Note individual steps can still override their getActionFor() method and
     * take priority over this ActionView.
     *
     * @param actionType
     *         - The type of action to get the action view for.
     * @return The default ActionView for the given ActionType.
     */
    @Nullable
    public ActionView getActionFor(@ActionType String actionType) {
        // By default we have no task default ActionViews.
        return null;
    }

    @NonNull
    public LiveData<Step> getStep() {
        return currentStepLiveData;
    }

    public StepNavigator getStepNavigator() {
        return this.stepNavigator;
    }

    @NonNull
    public LiveData<StepView> getStepView() {
        return stepViewLiveData;
    }

    public Task getTask() {
        return task;
    }

    @NonNull
    public LiveData<TaskInfo> getTaskInfo() {
        return taskLiveData;
    }

    @NonNull
    public LiveData<TaskProgress> getTaskProgress() {
        return taskProgressLiveData;
    }

    @NonNull
    public LiveData<TaskResult> getTaskResult() {
        return taskResultLiveData;
    }

    @NonNull
    public TaskView getTaskView() {
        return taskView;
    }

    /**
     * Navigates backward in the task without writing a result for the current step.
     */
    public void goBack() {
        LOGGER.debug("goBack called");
        Step currentStep = currentStepLiveData.getValue();
        TaskResult taskResult = taskResultLiveData.getValue();
        checkState(currentStep != null);
        checkState(taskResult != null);

        Step backStep = stepNavigator.getPreviousStep(currentStep, taskResult);
        if (backStep != null) {
            this.updateCurrentStep(backStep, taskResult);
        } else {
            LOGGER.warn("goBack called from first step");
        }
    }

    /**
     * Navigates forward in the task writing a result for the current step.
     */
    public void goForward() {
        LOGGER.debug("goForward called");
        Step currentStep = currentStepLiveData.getValue();
        TaskResult taskResult = taskResultLiveData.getValue();
        checkState(taskResult != null);
        if (currentStep != null) {
            Result previousResult = taskResult.getResult(currentStep);
            if (previousResult == null) {
                // If for whatever reason the step didn't create a result matching it's identifier we create a ResultBase
                // to mark that the step completed.
                // TODO rkolmos 08/08/2018 fix the result start time to be correct.
                this.addStepResult(new ResultBase(currentStep.getIdentifier(), Instant.now(), Instant.now()));
            }
        }

        Step nextStep = stepNavigator.getNextStep(currentStep, taskResult);
        this.updateCurrentStep(nextStep, taskResult);
    }

    /**
     * Returns true if there is a step after the current one in the task, false otherwise.
     *
     * @return true if there is a step after the current one in the task, false otherwise.
     */
    public boolean hasNextStep() {
        TaskResult taskResult = taskResultLiveData.getValue();
        checkState(taskResult != null);
        return this.stepNavigator.getNextStep(this.getStep().getValue(), taskResult) != null;
    }

    /**
     * Returns true if there is a step before the current one in the task, false otherwise.
     *
     * @return true if there is a step before the current one in the task, false otherwise.
     */
    public boolean hasPreviousStep() {
        Step currentStep = currentStepLiveData.getValue();
        checkState(currentStep != null);
        TaskResult taskResult = taskResultLiveData.getValue();
        checkState(taskResult != null);
        return this.stepNavigator.getPreviousStep(currentStep, taskResult) != null;
    }

    protected void onCleared() {
        compositeDisposable.dispose();
    }

    /**
     * Sets the value of taskProgress, currentStep, and stepView live datas to match switching the step to the given
     * next step.
     *
     * @param nextStep
     *         The step to use as the new current step.
     * @param taskResult
     *         The task result before this switch occured.
     */
    protected void updateCurrentStep(@Nullable Step nextStep, @NonNull TaskResult taskResult) {
        if (nextStep == null) {
            this.currentStepLiveData.setValue(null);
            this.stepViewLiveData.setValue(null);
            this.taskProgressLiveData.setValue(null);
        } else {
            TaskProgress nextProgress = this.stepNavigator.getProgress(nextStep, taskResult);
            this.taskProgressLiveData.setValue(nextProgress);
            LOGGER.debug("Setting step: {}", nextStep);
            this.currentStepLiveData.setValue(nextStep);
            StepView stepView = this.stepViewMapping.get(nextStep);
            this.stepViewLiveData.setValue(stepView);
        }
    }

    @VisibleForTesting
    void handleTaskLoad(Task task) {
        LOGGER.debug("Loaded task: {}", task);
        this.task = task;
        stepNavigator = stepNavigatorFactory.create(task, task.getProgressMarkers());
        this.stepViewMapping = new HashMap<>();
        for (Step step : this.stepNavigator.getSteps()) {
            // This if statement is necessary to ensure we can call stepViewFactory.apply on the step.
            if (step instanceof ThemedUIStep) {
                this.stepViewMapping.put(step, stepViewFactory.apply(step));
            }
        }
    }

    @VisibleForTesting
    void handleTaskResultFound(TaskResult taskResult) {
        LOGGER.debug("Loaded taskResult: {}", taskResult);
        if (this.lastRun != null) {
            Result lastRunResult = new AnswerResultBase<>(LAST_RUN_RESULT_ID, Instant.now(), Instant.now(),
                    this.lastRun,
                    AnswerResultType.DATE);
            taskResult = taskResult.addAsyncResult(lastRunResult);
        }

        taskResultLiveData.setValue(taskResult);
    }

    // TODO: Make this private and have Fragment call, instead of calling in constructor. This should make it easier to test
    @VisibleForTesting
    void initTaskSteps(TaskView taskView, UUID taskRunUuid) {
        compositeDisposable.add(
                Completable.mergeArray(
                        taskRepository.getTask(taskView.getIdentifier())
                                .doOnSuccess(this::handleTaskLoad)
                                .toCompletable(),
                        taskRepository.getTaskResult(taskRunUuid)
                                .toSingle(new TaskResultBase(taskView.getIdentifier(), Instant.now(), taskRunUuid))
                                .doOnSuccess(this::handleTaskResultFound)
                                .toCompletable()
                ).subscribe(this::taskInitSuccess, this::taskInitFail)
        );
    }

    @VisibleForTesting
    void taskInitFail(Throwable t) {
        LOGGER.warn("Failed to init task", t);
    }

    @VisibleForTesting
    void taskInitSuccess() {
        // TODO if there is a TaskResult with a task path (from a previous taskRunUuid), set current step to the last
        // step the user was at @liujoshua 2018/08/07
        goForward();
    }
}
