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

import static android.arch.lifecycle.LiveDataReactiveStreams.fromPublisher;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.INotificationSideChannel;

import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.AnswerResultType;
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.interfaces.ThemedUIStep;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfoView;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.inject.RecorderModule.RecorderFactory;
import org.sagebionetworks.research.presentation.inject.StepViewModule.StepViewFactory;
import org.sagebionetworks.research.presentation.mapper.TaskMapper;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView.NavDirection;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModelFactory.SharedPrefsArgs;
import org.sagebionetworks.research.presentation.perform_task.TaskResultManager.TaskResultManagerConnection;
import org.sagebionetworks.research.presentation.perform_task.TaskResultService.TaskResultServiceBinder;
import org.sagebionetworks.research.presentation.recorder.service.RecorderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@MainThread
public class PerformTaskViewModel extends AndroidViewModel {
    /**
     * Holds state from initial task load.
     */
    @VisibleForTesting
    static class TaskLoadHolder {
        public final TaskResult initialTaskResult;

        public final Task task;

        public final TaskResultServiceBinder taskResultServiceBinder;

        private TaskLoadHolder(final Task task,
                final TaskResultServiceBinder taskResultServiceBinder,
                final TaskResult initialTaskResult) {
            this.task = task;
            this.taskResultServiceBinder = taskResultServiceBinder;
            this.initialTaskResult = initialTaskResult;
        }
    }

    public static final String LAST_RUN_RESULT_ID = "lastRun";

    public static final String RUN_COUNT_RESULT_ID = "runCount";

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformTaskViewModel.class);

    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<Step> currentStepLiveData;

    private final SharedPrefsArgs sharedPrefsArgs;

    private final RecorderConfigPresentationFactory recorderConfigPresentationFactory;

    private RecorderManager recorderManager;

    private StepNavigator stepNavigator;

    private final StepNavigatorFactory stepNavigatorFactory;

    private final StepViewFactory stepViewFactory;

    private final MutableLiveData<StepView> stepViewLiveData;

    private final Map<Step, StepView> stepViewMapping;

    private Task task;

    private final MutableLiveData<TaskInfoView> taskInfoViewMutableLiveData;

    private final MutableLiveData<TaskProgress> taskProgressLiveData;

    private final TaskRepository taskRepository;

    private final LiveData<TaskResult> taskResultLiveData;

    private TaskResultManager taskResultManager;

    private Single<TaskResultManagerConnection> taskResultManagerConnectionSingle;

    private final UUID taskRunUuid;

    private final TaskView taskView;

    public PerformTaskViewModel(@NonNull Application application, @NonNull TaskView taskView,
            @NonNull UUID taskRunUUID, @NonNull StepNavigatorFactory stepNavigatorFactory,
            @NonNull TaskRepository taskRepository, @NonNull TaskMapper taskMapper,
            @NonNull RecorderFactory recorderFactory,
            @NonNull RecorderConfigPresentationFactory recorderConfigPresentationFactory,
            @NonNull StepViewFactory stepViewFactory,
            @NonNull TaskResultProcessingManager taskResultProcessingManager,
            @NonNull TaskResultManager taskResultManager,
            @Nullable SharedPrefsArgs sharedPrefsArgs) {
        super(application);
        this.recorderConfigPresentationFactory = checkNotNull(recorderConfigPresentationFactory);
        this.taskView = checkNotNull(taskView);
        this.taskRunUuid = checkNotNull(taskRunUUID);
        this.stepNavigatorFactory = checkNotNull(stepNavigatorFactory);
        this.taskRepository = checkNotNull(taskRepository);
        this.stepViewFactory = checkNotNull(stepViewFactory);
        this.taskResultManager = taskResultManager;
        this.sharedPrefsArgs = sharedPrefsArgs;

        // TODO migrate these LiveData to StepNavigationViewModel @liujoshua 2018/08/07

        taskProgressLiveData = new MutableLiveData<>();
        taskProgressLiveData.setValue(null);

        currentStepLiveData = new MutableLiveData<>();
        currentStepLiveData.setValue(null);

        stepViewLiveData = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();

        taskInfoViewMutableLiveData = new MutableLiveData<>();

        stepViewMapping = new HashMap<>();

        taskResultManagerConnectionSingle = taskResultManager
                .getTaskResultManagerConnection(taskView.getIdentifier(), taskRunUUID);

        taskResultLiveData = fromPublisher(
                taskResultManagerConnectionSingle
                        .flatMapObservable(TaskResultManagerConnection::getTaskResultObservable)
                        .toFlowable(BackpressureStrategy.LATEST));
        // we need something to get updates to this LiveData
        taskResultLiveData
                .observeForever(this::taskResultObserver);
        taskResultProcessingManager.registerTaskRun(taskView.getIdentifier(), taskRunUuid);

        initWithTask(taskView);
    }

    public void addStepResult(Result result) {
        taskResultManagerConnectionSingle.blockingGet().addStepResult(result);
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

    @NonNull
    public LiveData<StepView> getStepView() {
        return stepViewLiveData;
    }

    public Task getTask() {
        // TODO: remove @liujoshua 08/24/2018
        return task;
    }

    @NonNull
    public LiveData<TaskInfoView> getTaskInfoView() {
        return taskInfoViewMutableLiveData;
    }

    @NonNull
    public LiveData<TaskProgress> getTaskProgress() {
        return taskProgressLiveData;
    }

    @NonNull
    public LiveData<TaskResult> getTaskResultLiveData() {
        return taskResultLiveData;
    }

    @NonNull
    public TaskResult getTaskResult() {
        return taskResultManagerConnectionSingle.blockingGet().getLatestTaskResult();
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
        TaskResult taskResult = taskResultManagerConnectionSingle.blockingGet().getLatestTaskResult();
        checkState(currentStep != null);

        Step backStep = stepNavigator.getPreviousStep(currentStep, taskResult);
        if (backStep != null) {
            this.recorderManager.onStepTransition(currentStep, backStep, NavDirection.SHIFT_RIGHT);
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
        TaskResult taskResult = taskResultManagerConnectionSingle.blockingGet().getLatestTaskResult();

        Step nextStep = stepNavigator.getNextStep(currentStep, taskResult);
        this.recorderManager.onStepTransition(currentStep, nextStep, NavDirection.SHIFT_LEFT);
        this.updateCurrentStep(nextStep, taskResult);
    }

    /**
     * Returns true if there is a step after the current one in the task, false otherwise.
     *
     * @return true if there is a step after the current one in the task, false otherwise.
     */
    public boolean hasNextStep() {
        TaskResult taskResult = taskResultManagerConnectionSingle.blockingGet().getLatestTaskResult();
        checkState(taskResult != null);
        return stepNavigator.getNextStep(this.getStep().getValue(), taskResult) != null;
    }

    /**
     * Returns true if there is a step before the current one in the task, false otherwise.
     *
     * @return true if there is a step before the current one in the task, false otherwise.
     */
    public boolean hasPreviousStep() {
        Step currentStep = currentStepLiveData.getValue();
        checkState(currentStep != null);
        TaskResult taskResult = taskResultManagerConnectionSingle.blockingGet().getLatestTaskResult();
        checkState(taskResult != null);
        return stepNavigator.getPreviousStep(currentStep, taskResult) != null;
    }

    @Override
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
            taskResultManagerConnectionSingle.blockingGet().finishTask();
        } else {
            TaskProgress nextProgress = stepNavigator.getProgress(nextStep, taskResult);
            this.taskProgressLiveData.setValue(nextProgress);
            LOGGER.debug("Setting step: {}", nextStep);
            this.currentStepLiveData.setValue(nextStep);
            StepView stepView = this.stepViewMapping.get(nextStep);
            if (stepView == null) {
                LOGGER.warn("Step not found");
            }
            this.stepViewLiveData.setValue(stepView);
        }
    }

    void cacheStepViews(List<Step> steps) {
        for (Step step : steps) {
            // This if statement is necessary to ensure we can call stepViewFactory.apply on the step.
            if (step instanceof SectionStep) {
                cacheStepViews(((SectionStep) step).getSteps());
            } else {
                try {
                    // We attempt to create a step view from the step.
                    StepView stepView = stepViewFactory.apply(step);
                    stepViewMapping.put(step, stepView);
                } catch (IllegalArgumentException e) {
                    // If the call to the stepViewFactory fails we provide a warning.
                    LOGGER.warn("Unknown step type: {}", step);
                }

            }
        }
    }

    // TODO: Make this private and have Fragment call, instead of calling in constructor. This should make it easier to test
    @VisibleForTesting
    void initWithTask(TaskView taskView) {
        compositeDisposable.add(
                taskRepository.getTask(taskView.getIdentifier())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::taskInitSuccess, this::taskInitFail));
    }

    @VisibleForTesting
    void taskInitFail(Throwable t) {
        LOGGER.warn("Failed to init task", t);

        // TODO: live data for error state
    }

    @VisibleForTesting
    @MainThread
    void taskInitSuccess(Task task) {
        // TODO if there is a TaskResult with a task path (from a previous taskRunUuid), set current step to the last
        // step the user was at @liujoshua 2018/08/07

        this.task = task;
        recorderManager = new RecorderManager(task, taskView.getIdentifier(), taskRunUuid, getApplication(),
                taskResultManager, recorderConfigPresentationFactory);
        // Subscribe to the recorder results and put them in the async results.

        stepNavigator = stepNavigatorFactory.create(task, task.getProgressMarkers());

        // eagerly cache step views
        cacheStepViews(task.getSteps());

        // wait to see a task result, which originates from TaskResultService
        taskResultLiveData.observeForever(new Observer<TaskResult>() {
            @Override
            public void onChanged(@Nullable final TaskResult taskResult) {
                LOGGER.debug("taskResult: {}", taskResult);
                compositeDisposable.add(
                        taskResultManagerConnectionSingle
                                .subscribe((resultManagerConnection) -> {
                                    if (sharedPrefsArgs != null) {
                                        resultManagerConnection.addAsyncActionResult(Maybe.fromCallable(
                                                () -> new AnswerResultBase<>(LAST_RUN_RESULT_ID, Instant.now(), Instant.now(),
                                                        sharedPrefsArgs.lastRun, AnswerResultType.DATE)));
                                        resultManagerConnection.addAsyncActionResult(Maybe.fromCallable(
                                                () -> new AnswerResultBase<>(RUN_COUNT_RESULT_ID, Instant.now(), Instant.now(),
                                                        sharedPrefsArgs.runCount, AnswerResultType.INTEGER)));
                                    }
                                    goForward();
                                }, t -> taskInitFail(t)));

                taskResultLiveData.removeObserver(this);
            }
        });
    }

    void taskResultObserver(TaskResult taskResult) {
        LOGGER.debug("Observed TaskResult: {}", taskResult);
    }
}
