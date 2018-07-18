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

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.ActionType;
import org.sagebionetworks.research.presentation.DisplayDrawable;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.AnimationImageThemeView;
import org.sagebionetworks.research.presentation.model.ImageThemeView;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.action.ActionViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.FetchableImageThemeView;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel;

import java.util.List;

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
        TaskProgress progress = this.performTaskViewModel.getTaskProgress().getValue();
        if (progress != null) {
            ProgressBar progressBar = this.stepViewBinding.getProgressBar();
            if (progressBar != null) {
                progressBar.setMax(progress.getTotal());
                progressBar.setProgress(progress.getProgress());
            }

            TextView progressLabel = this.stepViewBinding.getProgressLabel();
            if (progressLabel != null) {
                String progressString = "STEP " + progress.getProgress() + " OF " + progress.getTotal();
                progressLabel.setText(progressString);
            }
        }

        ImageView imageView = this.stepViewBinding.getImageView();
        if (imageView != null) {
            ImageThemeView imageTheme = stepView.getImageTheme();
            if (imageTheme != null) {
                if (imageTheme instanceof AnimationImageThemeView) {
                    AnimationImageThemeView animationImageTheme = ((AnimationImageThemeView) imageTheme);
                    List<DisplayDrawable> drawables = animationImageTheme.getImageResources();
                    int duration = (int) ((animationImageTheme.getDuration() * 1000) / drawables.size());
                    AnimationDrawable animation = new AnimationDrawable();
                    for (DisplayDrawable displayDrawable : drawables) {
                        Integer drawableRes = displayDrawable.getDrawable();
                        if (drawableRes != null) {
                            Drawable drawable = this.getResources().getDrawable(drawableRes);
                            if (drawable != null) {
                                animation.addFrame(drawable, duration);
                            }
                        }
                    }

                    imageView.setImageDrawable(animation);
                    animation.start();
                } else if (imageTheme instanceof FetchableImageThemeView) {
                    DisplayDrawable drawable = ((FetchableImageThemeView) imageTheme).getImageResource();
                    Integer imageResourceId = drawable.getDrawable();
                    if (imageResourceId != null) {
                        imageView.setImageResource(imageResourceId);
                    } else {
                        System.err.println("DisplayDrawable has null drawableRes and null defaultDrawableRes");
                    }
                }
            }
        }
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
        ActionButton forwardButton = this.stepViewBinding.getNextButton();
        ActionView forwardActionView = this.getForwardButtonActionView(stepView);
        this.updateButtonFromActionView(forwardButton, forwardActionView);
        ActionButton backButton = this.stepViewBinding.getBackButton();
        ActionView backActionView = this.getBackwardButtonActionView(stepView);
        this.updateButtonFromActionView(backButton, backActionView);
        ActionButton cancelButton = this.stepViewBinding.getCancelButton();
        ActionView cancelActionView = this.getCancelButtonActionView(stepView);
        this.updateButtonFromActionView(cancelButton, cancelActionView);
        ActionButton skipButton = this.stepViewBinding.getSkipButton();
        ActionView skipActionView = this.getSkipButtonActionView(stepView);
        this.updateButtonFromActionView(skipButton, skipActionView);
        ActionButton infoButton = this.stepViewBinding.getInfoButton();
        ActionView infoActionView = this.getInfoButtonActionView(stepView);
        this.updateButtonFromActionView(infoButton, infoActionView);
    }
    // endregion
}
