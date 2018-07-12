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

import static com.google.common.base.Preconditions.checkNotNull;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
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
import org.sagebionetworks.research.mobile_ui.inject.ShowStepFragmentModule.ShowStepFragmentFactory;
import org.sagebionetworks.research.mobile_ui.show_step.view.FragmentSkipRule;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView.NavDirection;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModelFactory;
import org.sagebionetworks.research.presentation.show_step.show_step_view_model_factories.ShowStepViewModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.zone.ZoneRulesException;

import java.io.Serializable;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * A placeholder fragment containing a simple view.
 */
public class PerformTaskFragment extends Fragment implements HasSupportFragmentInjector, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformTaskFragment.class);

    private static final String ARGUMENT_TASK_VIEW = "TASK_VIEW";

    private static final String ARGUMENT_TASK_RUN_UUID = "TASK_RUN_UUID";

    public static final String LAST_RUN_KEY = "LAST_RUN";


    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    ShowStepFragmentFactory showStepFragmentFactory;

    @Inject
    PerformTaskViewModelFactory taskViewModelFactory;

    @Inject
    ShowStepViewModelFactory showStepViewModelFactory;

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

    public PerformTaskFragment() {
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
                .of(this, taskViewModelFactory.create(taskView, taskRunParcelableUuid.getUuid(),
                        this.getLastRun()))
                .get(PerformTaskViewModel.class);
        performTaskViewModel.getStepView().observe(this, this::showStep);
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
            }

            // Write this date as the new last run.
            String sharedPreferencesKey = this.taskView.getIdentifier();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(sharedPreferencesKey,
                    Context.MODE_PRIVATE);
            sharedPreferences.edit().putLong(LAST_RUN_KEY, Instant.now().toEpochMilli()).apply();
            // TODO handle end of perform task.
            return;
        }

        ShowStepFragmentBase step = showStepFragmentFactory.create(stepView, this);
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

        if (step instanceof FragmentSkipRule) {
            FragmentSkipRule fragmentSkipRule = (FragmentSkipRule)step;
            if (fragmentSkipRule.shouldSkip()) {
                String skipToIdentifier = fragmentSkipRule.skipToIdentifier();
                if (skipToIdentifier != null) {
                    // If we have the identifier of the step to skip to we use that.
                    this.performTaskViewModel.skipToStep(fragmentSkipRule.skipToIdentifier());
                } else {
                    // Otherwise we just go forward.
                    this.performTaskViewModel.goForward();
                }

                return;
            }
        }

    }

    private ZonedDateTime getLastRun() {
        String sharedPreferencesKey = this.taskView.getIdentifier();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(sharedPreferencesKey,
                Context.MODE_PRIVATE);
        Long lastRun = sharedPreferences.getLong(LAST_RUN_KEY, 0);
        if (lastRun == 0) {
            return null;
        }

        Instant lastRunInstant = Instant.ofEpochMilli(lastRun);
        ZoneId zoneId = null;
        try {
            zoneId = ZoneId.systemDefault();
        } catch (ZoneRulesException e) {
            zoneId = ZoneId.of("Z"); // UTC time.
        }

        return ZonedDateTime.ofInstant(lastRunInstant, zoneId);
    }
}
