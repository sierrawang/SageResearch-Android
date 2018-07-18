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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.StepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.ActionType;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.show_step.show_step_view_model_factories.ShowStepViewModelFactory;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowStepViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

/**
 * A ShowStepFragmentBase implements the functionality common to showing all step fragments in terms of 2 other
 * unknown operations (instantiateBinding, getLayoutID).
 *
 * @param <S>
 *         The type of StepView that this fragment uses.
 * @param <VM>
 *         The type of StepViewModel that this fragment uses.
 * @param <SB>
 *         The type of StepViewBinding that this fragment uses.
 */
public abstract class ShowStepFragmentBase
        <S extends StepView, VM extends ShowStepViewModel<S>, SB extends StepViewBinding<S>>
        extends Fragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowStepFragmentBase.class);

    private static final String ARGUMENT_STEP_VIEW = "STEP_VIEW";

    private static final String ARGUMENT_TASK_FRAGMENT = "TASK_FRAGMENT";

    protected PerformTaskFragment performTaskFragment;

    protected PerformTaskViewModel performTaskViewModel;

    protected VM showStepViewModel;

    protected S stepView;

    protected SB stepViewBinding;

    protected boolean initialized;

    private Unbinder stepViewUnbinder;

    /**
     * Creates a Bundle containing the given StepView.
     *
     * @param stepView
     *         The StepView to put in the bundle.
     * @return a Bundle containing the given StepView.
     */
    public static Bundle createArguments(@NonNull StepView stepView,
                                         @NonNull PerformTaskFragment performTaskFragment) {
        checkNotNull(stepView);
        checkNotNull(performTaskFragment);

        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_STEP_VIEW, stepView);
        args.putSerializable(ARGUMENT_TASK_FRAGMENT, performTaskFragment);
        return args;
    }

    protected void initialize() {
        if (!this.initialized) {
            if (getArguments() != null) {
                this.performTaskFragment = (PerformTaskFragment)
                        this.getArguments().getSerializable(ARGUMENT_TASK_FRAGMENT);
                this.stepView = this.getArguments().getParcelable(ARGUMENT_STEP_VIEW);
            } else {
                LOGGER.warn("ShowStepFragment created with null arguments: {}", this);
                return;
            }

            if (this.performTaskFragment != null) {
                // gets the PerformTaskViewModel instance of performTaskFragment
                this.performTaskViewModel = ViewModelProviders.of(this.performTaskFragment)
                        .get(PerformTaskViewModel.class);
            } else {
                LOGGER.warn("ShowStepFragment not passed PerformTaskFragment: {}", this);
            }
        }

        this.initialized = true;
    }

    public ShowStepFragmentBase() {
        this.initialized = false;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initialize();

        if (this.stepView != null) {
            ShowStepViewModelFactory showStepViewModelFactory = this.performTaskViewModel
                    .getShowStepViewModelFactory();
            //noinspection unchecked
            this.showStepViewModel = (VM) ViewModelProviders
                    .of(this, showStepViewModelFactory.create(this.performTaskViewModel,
                            this.stepView))
                    .get(this.stepView.getIdentifier(),
                            showStepViewModelFactory.getViewModelClass(this.stepView));
        } else {
            LOGGER.warn("ShowStepFragment not passed StepView: {}", this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(this.getLayoutId(), container, false);
        this.stepViewBinding = this.instantiateAndBindBinding(view);
        this.stepViewBinding.setActionButtonClickListener(this::handleActionButtonClick);
        this.showStepViewModel.getStepView().observe(this, this::update);
        this.stepViewBinding.setActionButtonClickListener(this::handleActionButtonClick);
        this.update(this.showStepViewModel.getStepView().getValue());
        return view;
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
            this.performTaskFragment.endPerformTask();
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
    protected abstract SB instantiateAndBindBinding(View view);

    protected void update(S stepView) {
        this.stepViewBinding.update(stepView);
    }
}
