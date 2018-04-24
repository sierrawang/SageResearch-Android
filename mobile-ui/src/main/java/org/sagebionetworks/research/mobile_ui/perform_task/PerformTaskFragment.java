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
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.mapper.StepMapper;
import org.sagebionetworks.research.mobile_ui.show_step.StepPresenterFactory;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.presentation.model.StepView;
import org.sagebionetworks.research.presentation.model.StepView.NavDirection;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModelFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * A placeholder fragment containing a simple view.
 */
public class PerformTaskFragment extends Fragment implements HasSupportFragmentInjector {
    private static final String ARGUMENT_TASK_IDENTIFIER = "TASK_VIEW";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    StepMapper stepMapper;

    @Inject
    StepPresenterFactory stepPresenterFactory;

    @Inject
    PerformTaskViewModelFactory taskViewModelFactory;

    private ShowStepFragmentBase currentStepFragment;

    private PerformTaskViewModel performTaskViewModel;

    private String taskIdentifier;

    private Unbinder unbinder;

    public static PerformTaskFragment newInstance(@NonNull String taskIdentifier) {
        checkNotNull(taskIdentifier);

        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TASK_IDENTIFIER, taskIdentifier);

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
                taskIdentifier = getArguments().getString(ARGUMENT_TASK_IDENTIFIER);
            }
        } else {
            taskIdentifier = savedInstanceState.getString(ARGUMENT_TASK_IDENTIFIER);
        }
        checkState(!Strings.isNullOrEmpty(taskIdentifier), "taskIdentifier cannot be null or empty");

        performTaskViewModel = ViewModelProviders.of(this, taskViewModelFactory.create(taskIdentifier))
                .get(PerformTaskViewModel.class);

        performTaskViewModel.getStep().observe(this, this::showStep);
        performTaskViewModel.goForward();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rs2_fragment_perform_task2, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(ARGUMENT_TASK_IDENTIFIER, taskIdentifier);
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
