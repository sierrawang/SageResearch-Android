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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.sagebionetworks.research.domain.presentation.model.LoadableResource;
import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.Result;
import org.sagebionetworks.research.domain.result.TaskResult;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfo;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.presentation.mapper.TaskMapper;
import org.sagebionetworks.research.presentation.model.BaseStepView;
import org.sagebionetworks.research.presentation.model.StepView;
import org.sagebionetworks.research.presentation.model.StepView.NavDirection;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.UUID;

import io.reactivex.disposables.CompositeDisposable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@MainThread
public class PerformTaskViewModel extends ViewModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformTaskViewModel.class);

    private final CompositeDisposable compositeDisposable;

    private final MutableLiveData<Step> currentStepLiveData;

    private StepNavigator stepNavigator;

    private final StepNavigatorFactory stepNavigatorFactory;

    private final MutableLiveData<StepView> stepViewLiveData;

    private final MutableLiveData<TaskInfo> taskLiveData;

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    @Nullable
    private TaskResult.Builder taskResultBuilder;

    private final MutableLiveData<TaskResult> taskResultLiveData;

    private final UUID taskRunUuid;

    private final TaskView taskView;

    private final MutableLiveData<LoadableResource<TaskView>> taskViewLiveData;

    public PerformTaskViewModel(@NonNull TaskView taskView, @NonNull UUID taskRunUUID,
            @NonNull StepNavigatorFactory stepNavigatorFactory, @NonNull TaskRepository taskRepository,
            @NonNull TaskMapper taskMapper) {
        this.taskView = checkNotNull(taskView);
        this.taskRunUuid = checkNotNull(taskRunUUID);
        this.stepNavigatorFactory = checkNotNull(stepNavigatorFactory);
        this.taskRepository = checkNotNull(taskRepository);
        this.taskMapper = checkNotNull(taskMapper);

        taskLiveData = new MutableLiveData<>();
        taskResultLiveData = new MutableLiveData<>();

        currentStepLiveData = new MutableLiveData<>();
        currentStepLiveData.setValue(null);

        stepViewLiveData = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        taskViewLiveData = new MutableLiveData<>();

        initTaskSteps(taskView, taskRunUuid);
    }

    public void addAsyncResult(Result result) {
        checkState(taskResultBuilder != null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addAsyncResult called with result: {}", result);
        }
        taskResultBuilder.addAsyncResult(result);
        taskResultLiveData.setValue(taskResultBuilder.build());
    }

    public void addStepResult(Result result) {
        checkState(taskResultBuilder != null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addStepResult called with result: {}", result);
        }
        taskResultBuilder.addStepResult(result);
        taskResultLiveData.setValue(taskResultBuilder.build());
    }

    @NonNull
    public LiveData<StepView> getStep() {
        return stepViewLiveData;
    }

    @NonNull
    public LiveData<TaskInfo> getTask() {
        return taskLiveData;
    }

    @NonNull
    public LiveData<TaskResult> getTaskResult() {
        return taskResultLiveData;
    }

    @NonNull
    public TaskView getTaskView() {
        return taskView;
    }

    public void goBack() {
        LOGGER.debug("goBack called");

        Step backStep = stepNavigator.getPreviousStep(currentStepLiveData.getValue(), taskResultBuilder.build());
        currentStepLiveData.setValue(backStep);
        stepViewLiveData.setValue(
                BaseStepView.builder()
                        .setIdentifier(backStep.getIdentifier())
                        .setNavDirection(NavDirection.SHIFT_RIGHT)
                        .build()
        );
    }

    public void goForward() {
        LOGGER.debug("goForward called");

        Step forwardStep = stepNavigator.getNextStep(currentStepLiveData.getValue(), taskResultBuilder.build());
        currentStepLiveData.setValue(forwardStep);
        stepViewLiveData.setValue(
                BaseStepView.builder()
                        .setIdentifier(forwardStep.getIdentifier())
                        .setNavDirection(NavDirection.SHIFT_LEFT)
                        .build()
        );
    }

    protected void onCleared() {
        compositeDisposable.dispose();
    }

    @VisibleForTesting
    void handleTaskLoad(Task task, Throwable t) {
        LOGGER.debug("Loaded task: {}", task);

        // TODO: initialize at last shown step, for when there starting with saved taskResult state
        if (t != null) {
            // TODO: handle in UI
            LOGGER.warn("Failed to load task steps", t);
        } else {
            stepNavigator = stepNavigatorFactory.create(task.getSteps());
            stepNavigator.getNextStep(null, taskResultBuilder.build());
        }
    }

    @VisibleForTesting
    void handleTaskResultFound(TaskResult taskResult) {
        taskResultBuilder = taskResult.toBuilder();
        taskResultLiveData.setValue(taskResultBuilder.build());
        taskResultBuilder = TaskResult.builder(taskRunUuid, Instant.now());
        taskResultLiveData.setValue(taskResultBuilder.build());
    }

    @VisibleForTesting
    void handleTaskResultLoadError(Throwable t) {
        // TODO: handle task result load error in UI
        LOGGER.warn("Failed to load TaskResult", t);
    }

    @VisibleForTesting
    void handleTaskResultMissing() {
        LOGGER.debug("No TaskResult found, using new TaskResult");
        taskResultBuilder = TaskResult.builder(taskRunUuid, Instant.now());
        taskResultLiveData.setValue(taskResultBuilder.build());
    }

    void initTaskSteps(TaskView taskView, UUID taskRunUuid) {
        compositeDisposable.add(taskRepository.getTask(taskView.getIdentifier())
                .subscribe(this::handleTaskLoad));

        compositeDisposable.add(taskRepository.getTaskResult(taskRunUuid)
                .subscribe(
                        this::handleTaskResultFound,
                        this::handleTaskResultLoadError,
                        this::handleTaskResultMissing));
    }
}
