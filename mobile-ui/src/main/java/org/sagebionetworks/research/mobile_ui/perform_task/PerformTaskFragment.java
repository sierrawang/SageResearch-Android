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

package org.sagebionetworks.research.mobile_ui.perform_task;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.presentation.model.StepView;
import org.sagebionetworks.research.presentation.model.StepView.NavDirection;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A placeholder fragment containing a simple view.
 */
public class PerformTaskFragment extends Fragment implements HasSupportFragmentInjector {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformTaskFragment.class);

    private static final String ARGUMENT_TASK_VIEW = "TASK_VIEW";

    private static final String ARGUMENT_TASK_RUN_UUID = "TASK_RUN_UUID";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    PerformTaskViewModelFactory taskViewModelFactory;

    private ShowStepFragmentBase currentStepFragment;

    private PerformTaskViewModel performTaskViewModel;

    private ParcelUuid taskRunParcelableUuid;

    private TaskView taskView;

    private Unbinder unbinder;

    public static PerformTaskFragment newInstance(@NonNull TaskView taskView, @Nullable UUID taskRunUUID) {
        checkNotNull(taskView);

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_TASK_VIEW, taskView);
        arguments.putParcelable(ARGUMENT_TASK_RUN_UUID, new ParcelUuid(taskRunUUID));

        PerformTaskFragment fragment = new PerformTaskFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                taskView = getArguments().getParcelable(ARGUMENT_TASK_VIEW);
                taskRunParcelableUuid = getArguments().getParcelable(ARGUMENT_TASK_RUN_UUID);
            }
        } else {
            taskView = savedInstanceState.getParcelable(ARGUMENT_TASK_VIEW);
            taskRunParcelableUuid = savedInstanceState.getParcelable(ARGUMENT_TASK_RUN_UUID);
        }

        checkNotNull(taskView, "taskView cannot be null");
        LOGGER.debug("taskView: {}", taskView);

        if (taskRunParcelableUuid == null) {
            LOGGER.debug("No taskRunUUID found, generating random UUID");
            taskRunParcelableUuid = new ParcelUuid(UUID.randomUUID());
        }

        TaskResult taskResult = new TaskResultBase("taskID", Instant.now(), UUID.randomUUID());
        performTaskViewModel = ViewModelProviders
                .of(this, taskViewModelFactory.create(taskView, taskRunParcelableUuid.getUuid()))
                .get(PerformTaskViewModel.class);

        performTaskViewModel.getStep().observe(this, this::showStep);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rs2_fragment_perform_task, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putParcelable(ARGUMENT_TASK_VIEW, taskView);
            outState.putParcelable(ARGUMENT_TASK_RUN_UUID, taskRunParcelableUuid);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @VisibleForTesting
    void showStep(StepView stepView) {
        if (stepView == null) {
            if (currentStepFragment != null) {
                getChildFragmentManager().beginTransaction().remove(currentStepFragment).commit();
                currentStepFragment = null;
            } else {
                // TODO: handle end of perform task
            }
            return;
        }
        ShowStepFragment step = ShowStepFragment.newInstance(stepView);

        currentStepFragment = step;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        if (NavDirection.SHIFT_LEFT == stepView.getNavDirection()) {
            transaction.setCustomAnimations(R.anim.rs2_right_slide_in, R.anim.rs2_left_slide_out);
        } else {
            transaction.setCustomAnimations(R.anim.rs2_left_slide_in, R.anim.rs2_right_slide_out);
        }

        transaction
                .replace(R.id.rs2_step_container, step, stepView.getIdentifier())
                .commit();
    }
}
