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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sagebionetworks.research.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.StepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.show_step.show_step_view_model_factories.AbstractShowStepViewModelFactory;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowStepViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A ShowStepFragmentBase implements the functionality common to showing all step fragments in terms of 2 other
 * unknown operations (instantiateBinding, getLayoutID).
 *
 * @param <StepT>
 *         The type of StepView that this fragment uses.
 * @param <ViewModelT>
 *         The type of StepViewModel that this fragment uses.
 */
public abstract class ShowStepFragmentBase<StepT extends StepView, ViewModelT extends ShowStepViewModel<StepT>,
        StepViewBindingT extends StepViewBinding<StepT>> extends Fragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowStepFragmentBase.class);

    private static final String ARGUMENT_STEP_VIEW = "STEP_VIEW";

    @Inject
    protected AbstractShowStepViewModelFactory abstractShowStepViewModelFactory;

    @Inject
    protected PerformTaskFragment performTaskFragment;

    protected PerformTaskViewModel performTaskViewModel;

    protected ViewModelT showStepViewModel;

    protected StepT stepView;

    protected StepViewBindingT stepViewBinding;

    /**
     * Creates a Bundle containing the given StepView.
     *
     * @param stepView
     *         The StepView to put in the bundle.
     * @return a Bundle containing the given StepView.
     */
    public static Bundle createArguments(@NonNull StepView stepView) {
        checkNotNull(stepView);

        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_STEP_VIEW, stepView);
        return args;
    }

    public ShowStepFragmentBase() {
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);

        // gets the PerformTaskViewModel instance of performTaskFragment
        this.performTaskViewModel = ViewModelProviders.of(performTaskFragment).get(PerformTaskViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StepT stepView = null;

        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                // noinspection unchecked
                stepView = (StepT) this.getArguments().getSerializable(ARGUMENT_STEP_VIEW);
            }
        } else {
            // noinspection unchecked
            stepView = (StepT) savedInstanceState.getSerializable(ARGUMENT_STEP_VIEW);
        }
        this.stepView = stepView;

        checkState(stepView != null, "stepView cannot be null");

        //noinspection unchecked
        this.showStepViewModel = (ViewModelT) ViewModelProviders
                .of(this, this.abstractShowStepViewModelFactory.create(this.performTaskViewModel, stepView))
                .get(stepView.getIdentifier(), this.abstractShowStepViewModelFactory.getViewModelClass(stepView));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutId(), container, false);
        this.stepViewBinding = this.instantiateAndBindBinding(view);
        this.stepViewBinding.setActionButtonClickListener(this::handleActionButtonClick);
        this.showStepViewModel.getStepView().observe(this, this::update);
        this.stepViewBinding.setActionButtonClickListener(this::handleActionButtonClick);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(ARGUMENT_STEP_VIEW, stepView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.stepViewBinding.unbind();
    }

    /**
     * Returns the ActionType corresponding to the given ActionButton or null if the ActionType cannot be found.
     * Default mapping of button id to ActionType is: rs2_step_navigation_action_forward -> ActionType.Forward
     * rs2_step_navigation_action_backward -> ActionType.Backward rs2_step_navigation_action_skip -> ActionType.Skip
     * rs2_step_header_cancel_button -> ActionType.CANCEL rs2_step_header_info_button -> ActionType.INFO
     *
     * @param actionButton
     *         The ActionButton to get the ActionType for.
     * @return the Actiontype corresponding to the given ActionButton or null if the ActionType cannot be found.
     */
    @Nullable
    @ActionType
    protected String getActionTypeFromActionButton(@NonNull ActionButton actionButton) {
        int actionButtonId = actionButton.getId();

        if (R.id.rs2_step_navigation_action_forward == actionButtonId) {
            return ActionType.FORWARD;
        } else if (R.id.rs2_step_navigation_action_backward == actionButtonId) {
            return ActionType.BACKWARD;
        } else if (R.id.rs2_step_navigation_action_skip == actionButtonId) {
            return ActionType.SKIP;
        } else if (R.id.rs2_step_navigation_action_cancel == actionButtonId) {
            return ActionType.CANCEL;
        } else if (R.id.rs2_step_navigation_action_info == actionButtonId) {
            return ActionType.INFO;
        }

        return null;
    }

    /**
     * Returns the layout resource that corresponds to the layout for this fragment.
     *
     * @return the layout resource that corresponds to the layout for this fragment.
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * Called whenever one of this fragment's ActionButton's is clicked. Subclasses should override to correctly
     * handle their ActionButtons.
     *
     * @param actionButton
     *         the ActionButton that was clicked by the user.
     */
    protected void handleActionButtonClick(@NonNull ActionButton actionButton) {
        @ActionType String actionType = this.getActionTypeFromActionButton(actionButton);
        if (actionType.equals(ActionType.CANCEL)) {
            boolean showDialog = false;
            if(this.performTaskViewModel.hasPreviousStep()) {
                showDialog = true;
            }
            this.performTaskFragment.cancelTask(showDialog);

        } else {
            this.showStepViewModel.handleAction(actionType);
        }
    }

    /**
     * Instantiates and returns and instance of the correct type of StepViewBinding for this fragment. Note: If a
     * subclass needs to add any fields to the binding it should override this method to return a different binding.
     *
     * @return An instance of the correct type of StepViewBinding for this fragment.
     */
    @NonNull
    protected abstract StepViewBindingT instantiateAndBindBinding(View view);

    protected void update(StepT stepView) {
        this.stepViewBinding.update(stepView);
    }
}
