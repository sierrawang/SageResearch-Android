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

package org.sagebionetworks.research.mobile_ui.step_views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.ui.ThemedUIStep;
import org.sagebionetworks.research.domain.step.ui.UIStep;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;


public class InstructionStepHeaderView extends ConstraintLayout {
    private Step step;
    private StepNavigator navigator;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView progressLabel;
    private TextView titleLabel;
    private TextView textLabel;
    private ImageButton cancelButton;

    public InstructionStepHeaderView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.commonInit(null, null);
    }

    public InstructionStepHeaderView(final Context context, final Step step, final StepNavigator navigator) {
        super(context);
        commonInit(step, navigator);
    }

    public InstructionStepHeaderView(final Context context, final AttributeSet attrs, final Step step,
            final StepNavigator navigator) {
        super(context, attrs);
        commonInit(step, navigator);
    }

    public InstructionStepHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr,
            final Step step, final StepNavigator navigator) {
        super(context, attrs, defStyleAttr);
        commonInit(step, navigator);
    }

    public Step getStep() {
        return step;
    }

    public StepNavigator getNavigator() {
        return navigator;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getProgressLabel() {
        return progressLabel;
    }

    public TextView getTitleLabel() {
        return titleLabel;
    }

    public TextView getTextLabel() {
        return textLabel;
    }

    public ImageButton getCancelButton() {
        return cancelButton;
    }

    private void commonInit(Step step, StepNavigator navigator) {
        this.step = step;
        this.navigator = navigator;
        inflate(this.getContext(), R.layout.mpower2_instruction_step_header, this);
        this.imageView = this.findViewById(R.id.imageView);
        this.progressBar = this.findViewById(R.id.progressBar);
        this.progressLabel = this.findViewById(R.id.progressLabel);
        this.titleLabel = this.findViewById(R.id.titleLabel);
        this.textLabel = this.findViewById(R.id.textLabel);
        this.cancelButton = this.findViewById(R.id.cancelButton);
        this.initializeComponents();
    }

    private void initializeComponents() {
        if (this.step instanceof ThemedUIStep) {
            ThemedUIStep themedStep = (ThemedUIStep) this.step;
            this.imageView.setImageResource(themedStep.getImageTheme().getImageResource());
        }

        if (this.step instanceof UIStep) {
            UIStep uiStep = (UIStep) this.step;
            this.titleLabel.setText(uiStep.getTitle());
            this.textLabel.setText(uiStep.getText());
        }
    }
}
