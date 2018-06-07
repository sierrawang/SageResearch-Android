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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.action.ActionViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel;

import java.util.Map;

public class ShowUIStepFragment extends
        ShowStepFragmentBase<UIStepView, ShowUIStepViewModel<UIStepView>, UIStepViewBinding<UIStepView>> {
    @NonNull
    public static ShowUIStepFragment newInstance(@NonNull StepView stepView) {
        if (!(stepView instanceof UIStepView)) {
            throw new IllegalArgumentException("Step view: " + stepView + " is not a UIStepView.");
        }

        ShowUIStepFragment fragment = new ShowUIStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    @Override
    protected UIStepViewBinding<UIStepView> instantiateBinding() {
        return new UIStepViewBinding<>();
    }

    @Override
    @LayoutRes
    public int getLayoutId() {
        return R.layout.rs2_show_ui_step_fragment_layout;
    }

    @Override
    protected void update(UIStepView stepView) {
        super.update(stepView);
        this.updateNavigationButtons(stepView);
    }

    protected void updateNavigationButtons(UIStepView stepView) {
        Map<String, ActionView> actions = stepView.getActions();
        StepNavigator stepNavigator = this.performTaskViewModel.getStepNavigator();
        if (this.stepViewBinding.nextButton != null) {
            ActionView nextButtonAction = actions.get("goForward");
            if (nextButtonAction != null && nextButtonAction.getButtonTitle() != null) {
                this.stepViewBinding.nextButton.setText(nextButtonAction.getButtonTitle().displayString);
            } else {
                if (stepNavigator.getNextStep(this.performTaskViewModel.getStep().getValue(),
                        this.performTaskViewModel.getTaskResult().getValue()) != null) {
                    // This is the last step.
                    this.stepViewBinding.nextButton.setText(R.string.rs2_navigation_action_forward_last_step);
                } else {
                    // This isn't the last step.
                    this.stepViewBinding.nextButton.setText(R.string.rs2_navigation_action_forward);
                }
            }
        }

        this.updateNavigationButton(stepView, this.stepViewBinding.skipButton, "skip",
                R.string.rs2_navigation_action_skip);
        this.updateNavigationButton(stepView, this.stepViewBinding.backButton, "goBack",
                R.string.rs2_navigation_action_backward);
        this.updateNavigationButton(stepView, this.stepViewBinding.cancelButton, "cancel",
                R.string.rs2_navigation_action_cancel);
        this.updateNavigationButton(stepView, this.stepViewBinding.infoButton, "info",
                R.string.rs2_navigation_action_info);
    }

    protected void updateNavigationButton(UIStepView StepView, ActionButton button, String actionKey, int defaultStringRes) {
        Map<String, ActionView> actions = stepView.getActions();
        if (button != null) {
            ActionView actionViewBase = actions.get(actionKey);
            if (actionViewBase != null && actionViewBase.getButtonTitle() != null) {
                button.setText(actionViewBase.getButtonTitle().displayString);
            } else {
                button.setText(defaultStringRes);
            }
        }
    }
}
