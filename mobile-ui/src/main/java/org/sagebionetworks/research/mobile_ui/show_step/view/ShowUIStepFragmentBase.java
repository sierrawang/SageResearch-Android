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

import android.view.View;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.ActionType;
import org.sagebionetworks.research.presentation.DisplayDrawable;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.action.ActionViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel;

public abstract class ShowUIStepFragmentBase<S extends UIStepView,
        VM extends ShowUIStepViewModel<S>, SB extends UIStepViewBinding<S>> extends
        ShowStepFragmentBase<S, VM, SB> {
    protected ActionView getBackwardButtonActionView(UIStepView stepView) {
        ActionView result = this.getDefaultActionView(stepView, ActionType.BACKWARD);
        if (result != null) {
            return result;
        }

        // If there is no previous step we will return null indicating the button should be hidden.
        if (this.performTaskViewModel.hasPreviousStep()) {
            String title = getResources().getString(R.string.rs2_navigation_action_backward);
            return ActionViewBase.builder().setButtonTitle(DisplayString.create(null, title)).build();
        } else {
            return null;
        }

    }

    protected ActionView getCancelButtonActionView(UIStepView stepView) {
        ActionView result = this.getDefaultActionView(stepView, ActionType.CANCEL);
        if (result != null) {
            return result;
        }

        // TODO rkolmos 06/10/2018
        Integer iconResId = R.drawable.rs2_cancel_icon;
        return ActionViewBase.builder().setButtonIcon(DisplayDrawable.create(null, iconResId)).build();
    }

    protected ActionView getDefaultActionView(UIStepView stepView, @ActionType String actionType) {
        ActionView result = stepView.getActionFor(actionType);
        // if the stepView has an action from the json then we use that.
        if (result != null) {
            return result;
        }

        // if the task view model has a default action this task precedent over the overall default
        result = this.performTaskViewModel.getActionFor(actionType);
        if (result != null) {
            return result;
        }

        return null;
    }

    protected ActionView getForwardButtonActionView(UIStepView stepView) {
        ActionView result = this.getDefaultActionView(stepView, ActionType.FORWARD);
        if (result != null) {
            return result;
        }

        // if neither the task nor the step has an action we use a default one.
        String title;
        if (this.performTaskViewModel.hasNextStep()) {
            title = getResources().getString(R.string.rs2_navigation_action_forward);
        } else {
            title = getResources().getString(R.string.rs2_navigation_action_forward_last_step);
        }

        return ActionViewBase.builder().setButtonTitle(DisplayString.create(null, title)).build();
    }

    protected ActionView getInfoButtonActionView(UIStepView stepView) {
        ActionView result = this.getDefaultActionView(stepView, ActionType.INFO);
        if (result != null) {
            return result;
        }

        // TODO rkolmos 06/10/2018 make this id not hardcoded.
        Integer iconResId = R.drawable.rs2_info_icon;
        return ActionViewBase.builder().setButtonIcon(DisplayDrawable.create(null, iconResId)).build();
    }

    protected ActionView getSkipButtonActionView(UIStepView stepView) {
        ActionView result = this.getDefaultActionView(stepView, ActionType.SKIP);
        if (result != null) {
            return result;
        }

        String title = this.getResources().getString(R.string.rs2_navigation_action_skip);
        return ActionViewBase.builder().setButtonTitle(DisplayString.create(null, title)).build();
    }

    @Override
    protected void update(S stepView) {
        super.update(stepView);
        this.updateNavigationButtons(stepView);
    }

    protected void updateButtonFromActionView(ActionButton button, ActionView actionView) {
        if (button != null) {
            if (actionView != null) {
                DisplayString buttonTitle = actionView.getButtonTitle();
                if (buttonTitle != null) {
                    String title = buttonTitle.getString(getContext().getResources());
                    if (title != null) {
                        button.setText(title);
                    }
                }

                DisplayDrawable buttonIcon = actionView.getButtonIcon();
                if (buttonIcon != null) {
                    Integer drawable = buttonIcon.getDrawable();
                    if (drawable != null) {
                        button.setBackgroundResource(drawable);
                    }
                }
            } else {
                // If the actionView is null this indicates that the button should be hidden.
                button.setVisibility(View.GONE);
            }
        }
    }

    // region Navigation Buttons
    protected void updateNavigationButtons(UIStepView stepView) {
        ActionButton skipButton = this.stepViewBinding.getSkipButton();
        this.updateButtonFromActionView(this.stepViewBinding.getNextButton(),
                this.getForwardButtonActionView(stepView));
        this.updateButtonFromActionView(this.stepViewBinding.getBackButton(),
                this.getBackwardButtonActionView(stepView));
        this.updateButtonFromActionView(this.stepViewBinding.getCancelButton(),
                this.getCancelButtonActionView(stepView));
        this.updateButtonFromActionView(skipButton, this.getSkipButtonActionView(stepView));
        this.updateButtonFromActionView(this.stepViewBinding.getInfoButton(), this.getInfoButtonActionView(stepView));
    }
    // endregion
}
