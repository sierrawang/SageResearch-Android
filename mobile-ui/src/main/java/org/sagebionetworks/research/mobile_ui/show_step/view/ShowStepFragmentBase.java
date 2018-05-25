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

package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.model.StepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.show_step.ShowStepViewModel;
import org.sagebionetworks.research.presentation.show_step.ShowStepViewModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A ShowStepFragmentBase implements the functionality common to showing all step fragments in terms of
 * four other unknown operations (instantiateBinding, getLayoutID, handleActionButtonClick, and update).
 * @param <S> The type of StepView that this fragment uses.
 * @param <VM> The type of StepViewModel that this fragment uses.
 */
public abstract class ShowStepFragmentBase<S extends StepView, VM extends ShowStepViewModel<S>> extends Fragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowStepFragmentBase.class);
    private static final String ARGUMENT_STEP_VIEW = "STEP_VIEW";

    @Inject
    protected PerformTaskFragment performTaskFragment;
    @Inject
    protected ShowStepViewModelFactory showStepViewModelFactory;

    protected PerformTaskViewModel performTaskViewModel;
    protected VM showStepViewModel;
    protected S stepView;
    protected UIStepViewBinding stepViewBinding;

    private Unbinder stepViewUnbinder;

    /**
     * Creates a Bundle containing the given StepView.
     * @param stepView The StepView to put in the bundle.
     * @return a Bundle containing the given StepView.
     */
    public static Bundle createArguments(@NonNull StepView stepView) {
        checkNotNull(stepView);

        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_STEP_VIEW, stepView);
        return args;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);

        // gets the PerformTaskViewModel instance of performTaskFragment
        this.performTaskViewModel = ViewModelProviders.of(this.performTaskFragment).get(PerformTaskViewModel.class);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        S stepViewArg = null;
        if (getArguments() != null) {
            stepViewArg = this.getArguments().getParcelable(ARGUMENT_STEP_VIEW);
        }

        //noinspection unchecked
        this.showStepViewModel = (VM) ViewModelProviders
                .of(this, this.showStepViewModelFactory.create(this.performTaskViewModel, stepViewArg))
                .get(stepViewArg.getIdentifier(), this.showStepViewModelFactory.getViewModelClass(stepViewArg));
        this.showStepViewModel.getStepView().observe(this, this::update);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(this.getLayoutId(), container, false);
        this.stepViewBinding = this.instantiateBinding();
        this.stepViewUnbinder = ButterKnife.bind(this.stepViewBinding, view);
        this.stepViewBinding.setActionButtonClickListener(this::handleActionButtonClick);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.stepViewUnbinder.unbind();
    }

    /**
     * Instantiates and returns and instance of the correct type of UIStepViewBinding for this fragment.
     * Note: If a subclass needs to add any fields to the binding it should override this method to return a
     * different binding.
     * @return An instance of the correct type of UIStepViewBinding for this fragment.
     */
    protected abstract UIStepViewBinding instantiateBinding();

    /**
     * Returns the layout resource that corresponds to the layout for this fragment.
     * @return the layout resource that corresponds to the layout for this fragment.
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * Called whenever one of this fragment's ActionButton's is clicked. Subclasses should override to correctly
     * handle their ActionButtons.
     * @param actionButton the ActionButton that was clicked by the user.
     */
    protected abstract void handleActionButtonClick(@NonNull ActionButton actionButton);

    /**
     * Updates the components of this fragment with the data from the given StepView. Subclasses should override to
     * correctly update all of their components.
     * @param stepView The StepView to get the data for the update from.
     */
    @VisibleForTesting
    protected abstract void update(S stepView);
}
