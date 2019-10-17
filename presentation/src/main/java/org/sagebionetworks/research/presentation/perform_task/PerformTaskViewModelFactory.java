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

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.presentation.inject.RecorderConfigPresentationFactory;
import org.sagebionetworks.research.presentation.inject.RecorderModule.RecorderFactory;
import org.sagebionetworks.research.presentation.inject.StepViewModule.StepViewFactory;
import org.sagebionetworks.research.presentation.mapper.TaskMapper;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.threeten.bp.ZonedDateTime;

import java.util.UUID;

import javax.inject.Inject;

public class PerformTaskViewModelFactory {
    /**
     * Class to encapsulate the arguments coming from SharedPreferences.
     */
    public static class SharedPrefsArgs {
        @Nullable
        public final ZonedDateTime lastRun;

        public final int runCount;

        public SharedPrefsArgs(final ZonedDateTime lastRun, final int runCount) {
            this.lastRun = lastRun;
            this.runCount = runCount;
        }
    }

    private final Application application;

    private final RecorderConfigPresentationFactory recorderConfigPresentationFactory;

    private final RecorderFactory recorderFactory;

    private final StepNavigatorFactory stepNavigatorFactory;

    private final StepViewFactory stepViewFactory;

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private TaskResultManager taskResultManager;

    private final TaskResultProcessingManager taskResultProcessingManager;

    @Inject
    public PerformTaskViewModelFactory(@NonNull Application application,
            @NonNull StepNavigatorFactory stepNavigatorFactory,
            @NonNull TaskMapper taskMapper, @NonNull TaskRepository taskRepository,
            @NonNull StepViewFactory stepViewFactory,
            @NonNull RecorderFactory recorderFactory,
            @NonNull RecorderConfigPresentationFactory recorderConfigPresentationFactory,
            @NonNull TaskResultProcessingManager taskResultProcessingManager,
            @NonNull TaskResultManager taskResultManager) {
        this.application = checkNotNull(application);
        this.stepNavigatorFactory = checkNotNull(stepNavigatorFactory);
        this.recorderFactory = checkNotNull(recorderFactory);
        this.recorderConfigPresentationFactory = checkNotNull(recorderConfigPresentationFactory);
        this.taskMapper = checkNotNull(taskMapper);
        this.taskRepository = checkNotNull(taskRepository);
        this.stepViewFactory = checkNotNull(stepViewFactory);
        this.taskResultProcessingManager = checkNotNull(taskResultProcessingManager);
        this.taskResultManager = taskResultManager;
    }

    public ViewModelProvider.Factory create(@NonNull TaskView taskView, @NonNull UUID taskRunUUID,
            SharedPrefsArgs sharedPrefsArgs) {
        checkNotNull(taskView);
        checkNotNull(taskRunUUID);

        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings(value = "unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(PerformTaskViewModel.class)) {
                    // noinspection unchecked
                    return (T) new PerformTaskViewModel(application, taskView, taskRunUUID, stepNavigatorFactory,
                            taskRepository, taskMapper, recorderFactory, recorderConfigPresentationFactory,
                            stepViewFactory, taskResultProcessingManager, taskResultManager, sharedPrefsArgs);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        };
    }
}
