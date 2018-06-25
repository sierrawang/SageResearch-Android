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

package org.sagebionetworks.research.mobile_ui.show_step.view.view_binding;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R2.id;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.mobile_ui.widget.NavigationActionBar;
import org.sagebionetworks.research.mobile_ui.widget.NavigationActionBar.ActionButtonClickListener;
import org.sagebionetworks.research.mobile_ui.widget.StepHeader;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A UIStepViewBinding stores the various views that a step view may contain. Supported Views are: NOTE: the format
 * for these is ID : TYPE - DESCRIPTION. - stepHeader : StepHeader - The header view for this view. - stepBody : View
 * - The body view for this view, usually has the majority of the content. - navigationActionBar : NavigationActionBar
 * - Contains navigation action buttons (forward, backward, skip). - imageView : ImageView - The view to display an
 * images for the step on. - titleLabel : TextView - The view to display the title of the step on. - textLabel :
 * TextView - The view to display the text of the step on. - detailLabel : TextView - The view to display the detail
 * of the step on. - footnoteLabel : TextView - The view to display the footnote of the step on. - progressLabel :
 * TextView - The view to display a String representation of the user's progress on. - progressBar : ProgressBar - The
 * view to display a Visual representation of the user's progress on. - cancelButton : Button - The button which ends
 * the current active task when pressed. - nextButton : Button - The button which causes the task to navigate to the
 * next step when pressed. - backButton : Button - The button which causes the task to navigate to the previous step
 * when pressed. - skipButton : Button - The button which causes the task to skip the current step when pressed.
 * (Note: this button is different then the next button because no result will be created for the current step)
 */
public class UIStepViewBinding<S extends UIStepView> implements StepViewBinding<S> {
    protected static class UIStepViewHolder {
        /**
         * Views can optionally have a button with the id `rs2_step_navigation_action_backward`. This button generally
         * causes the task to navigate to the previous step when pressed.
         */
        @Nullable
        @BindView(id.rs2_step_navigation_action_backward)
        public ActionButton backButton;

        /**
         * Views can optionally have a button with the id `rs2_step_navigation_action_cancel`. This button generally
         * ends the current active task when pressed
         */
        @Nullable
        @BindView(id.rs2_step_navigation_action_cancel)
        public ActionButton cancelButton;

        /**
         * Views can optionally have a text view with the id `rs2_detail`. This view generally displays further
         * information related to the information displayed on the textLabel.
         */
        @Nullable
        @BindView(id.rs2_detail)
        public TextView detail;

        /**
         * Views can optionally have a text view with the id `rs2_footnote`. This view generally displays the footnote
         * of the step that is being displayed.
         */
        @Nullable
        @BindView(id.rs2_footnote)
        public TextView footnote;

        /**
         * Views can optionally have an image view with the id `rs2_image_view`. This view generally displays and
         * image or icon that is associated with the step.
         */
        @Nullable
        @BindView(id.rs2_image_view)
        public ImageView imageView;

        /**
         * Views can optionally have a button with the id `rs2_step_navigation_action_info`. This button generally
         * displays more information about the step.
         */
        @Nullable
        @BindView(id.rs2_step_navigation_action_info)
        public ActionButton infoButton;

        /**
         * Views can optionally contain a NavigationActionBar to store and layout their navigation buttons.
         */
        @Nullable
        @BindView(id.rs2_step_navigation_action_bar)
        public NavigationActionBar navigationActionBar;

        /**
         * Views can optionally have a button with the id `rs2_step_navigation_action_forward`. This button generally
         * moves on to the next step in the active task, or completes the task when pressed.
         */
        @Nullable
        @BindView(id.rs2_step_navigation_action_forward)
        public ActionButton nextButton;

        /**
         * Views can optionally have a progress bar with the id `rs2_progress_bar. This progress bar generally
         * displays a visual indication of how much progress the user has made into the current active task. This
         * corresponds to the string displayed on the progressLabel.
         */
        @Nullable
        @BindView(id.rs2_progress_bar)
        public ProgressBar progressBar;

        /**
         * Views can optionally have a text view with the id `rs2_progress_label`. This view generally displays a
         * string indicating how much progress the user has made into the current active task (i.e. "STEP 1 OF 6").
         */
        @Nullable
        @BindView(id.rs2_progress_label)
        public TextView progressLabel;

        /**
         * Views can optionally have a button with the id `rs2_step_navigation_action_skip`. This button generally
         * causes the current step to be skipped when pressed.
         */
        @Nullable
        @BindView(id.rs2_step_navigation_action_skip)
        public ActionButton skipButton;

        /**
         * Views can optionally have a body view. This view generally contains the main content for the step such as
         * the title and text labels.
         */
        @Nullable
        @BindView(id.rs2_step_body)
        public View stepBody;

        /**
         * Views can optionally have a header view. This view generally contains the progress bar, and the cancel and
         * = info action buttons.
         */
        @Nullable
        @BindView(id.rs2_step_header)
        public StepHeader stepHeader;

        /**
         * Views can optionally have a text view with the id `rs2_text`. This view generally displays the main content
         * for the step in medium size font.
         */
        @Nullable
        @BindView(id.rs2_text)
        public TextView text;

        /**
         * Views can optionally have a title view with the id `rs2_title`. This view generally displays a large title
         * consisting of a brief description of what the step represents.
         **/
        @Nullable
        @BindView(id.rs2_title)
        public TextView title;
    }

    private final UIStepViewHolder uiStepViewHolder;

    private final Unbinder uiStepViewHolderUnbinder;

    public UIStepViewBinding(View view) {
        this.uiStepViewHolder = new UIStepViewHolder();
        this.uiStepViewHolderUnbinder = ButterKnife.bind(this.uiStepViewHolder, view);
    }

    @Nullable
    public ActionButton getBackButton() {
        return this.uiStepViewHolder.backButton;
    }

    @Nullable
    public ActionButton getCancelButton() {
        return this.uiStepViewHolder.cancelButton;
    }

    @Nullable
    public TextView getDetail() {
        return this.uiStepViewHolder.detail;
    }

    @Nullable
    public TextView getFoonote() {
        return this.uiStepViewHolder.footnote;
    }

    @Nullable
    public ImageView getImageView() {
        return this.uiStepViewHolder.imageView;
    }

    @Nullable
    public ActionButton getInfoButton() {
        return this.uiStepViewHolder.infoButton;
    }

    @Nullable
    public NavigationActionBar getNavigationActionBar() {
        return this.uiStepViewHolder.navigationActionBar;
    }

    @Nullable
    public ActionButton getNextButton() {
        return this.uiStepViewHolder.nextButton;
    }

    @Nullable
    public ProgressBar getProgressBar() {
        return this.uiStepViewHolder.progressBar;
    }

    @Nullable
    public TextView getProgressLabel() {
        return this.uiStepViewHolder.progressLabel;
    }

    @Nullable
    public ActionButton getSkipButton() {
        return this.uiStepViewHolder.skipButton;
    }

    @Nullable
    public View getStepBody() {
        return this.uiStepViewHolder.stepBody;
    }

    @Nullable
    public StepHeader getStepHeader() {
        return this.uiStepViewHolder.stepHeader;
    }

    @Nullable
    public TextView getText() {
        return this.uiStepViewHolder.text;
    }
    // endregion

    // region Getters
    @Nullable
    public TextView getTitle() {
        return this.uiStepViewHolder.title;
    }

    @Override
    public void setActionButtonClickListener(ActionButtonClickListener actionButtonClickListener) {
        NavigationActionBar actionBar = this.getNavigationActionBar();
        if (actionBar != null) {
            actionBar.setActionButtonClickListener(actionButtonClickListener);
        }

        StepHeader stepHeader = this.getStepHeader();
        if (stepHeader != null) {
            stepHeader.setActionButtonClickListener(actionButtonClickListener);
        }
    }

    @Override
    public void unbind() {
        this.uiStepViewHolderUnbinder.unbind();
    }

    @Override
    public void update(S stepView) {
        this.updateTextView(this.getTitle(), stepView.getTitle());
        this.updateTextView(this.getText(), stepView.getText());
        this.updateTextView(this.getDetail(), stepView.getDetail());
        this.updateTextView(this.getFoonote(), stepView.getFootnote());
        // TODO rkolmos 05/29/2018 implement color theme update
    }

    protected void updateTextView(TextView view, DisplayString displayString) {
        if (view != null) {
            if (displayString.getDisplayString() != null) {
                view.setText(displayString.getDisplayString());
            } else if (displayString.getDefaultDisplayStringRes() != null) {
                view.setText(displayString.getDefaultDisplayStringRes());
            }
        }
    }
}
