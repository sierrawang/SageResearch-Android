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

package org.sagebionetworks.research.mobile_ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.mobile_ui.R2;
import org.sagebionetworks.research.domain.mobile_ui.R2.id;
import org.sagebionetworks.research.mobile_ui.widget.NavigationActionBar.ActionButtonClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StepHeader extends ConstraintLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepHeader.class);
    private boolean isInfoButtonHidden;
    private boolean isProgressLabelHidden;
    private boolean isProgressBarHidden;
    private boolean isCancelButtonHidden;
    private boolean usesFloatingProgressBar;

    @BindView(id.rs2_step_navigation_action_cancel)
    @NonNull
    ActionButton cancelButton;
    @BindView(id.rs2_step_navigation_action_info)
    @NonNull
    ActionButton infoButton;
    @BindView(R2.id.rs2_step_header_progress_label)
    @NonNull
    TextView progressLabel;
    @BindView(R2.id.rs2_step_header_progress_bar)
    @NonNull
    ProgressBar progressBar;
    @BindView(R2.id.rs2_step_header_progress_bar_floating)
    @NonNull
    ProgressBar progressBarFloating;
    @BindView(R2.id.rs2_step_header_action_button_group)
    @NonNull
    ConstraintLayout actionButtonGroup;

    @NonNull
    private Unbinder unbinder;
    @Nullable
    private ActionButtonClickListener actionButtonClickListener;

    public StepHeader(final Context context) {
        super(context);
        this.commonInit(null);
    }

    public StepHeader(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.commonInit(attrs);
    }

    public StepHeader(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.commonInit(attrs);
    }

    protected void commonInit(@Nullable final AttributeSet attrs) {
        this.getAttributes(attrs);
        inflate(this.getContext(), R.layout.rs2_step_header, this);
        this.onFinishInflate();
    }

    public void setActionButtonClickListener(ActionButtonClickListener listener) {
        this.actionButtonClickListener = listener;
    }

    public void onActionButtonClick(@NonNull ActionButton actionButton) {
        LOGGER.debug("Action button clicked, text: {}", actionButton.getText());

        if (actionButtonClickListener != null) {
            actionButtonClickListener.onClick(actionButton);
        } else {
            LOGGER.debug("Action button clicked with null listener: {}", actionButton.getText());
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        this.unbinder = ButterKnife.bind(this);
        this.layoutComponents();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unbinder.unbind();
    }

    protected void layoutComponents() {
        if (this.isCancelButtonHidden) {
            this.cancelButton.setVisibility(View.GONE);
        }

        if (this.isInfoButtonHidden) {
            this.infoButton.setVisibility(View.GONE);
        }

        // We hide both progress bars whenever isProgressBarHidden is true.
        if (this.isProgressBarHidden) {
            this.progressBar.setVisibility(View.GONE);
            this.progressBarFloating.setVisibility(View.GONE);
        }

        if (this.isProgressLabelHidden) {
            this.progressLabel.setVisibility(View.GONE);
        }

        // If we are using the floating progress bar we hide both non-floating progress views, otherwise
        // if we also aren't using the cancel and info buttons we hide the entire action button group.
        if (this.usesFloatingProgressBar) {
            this.progressBar.setVisibility(View.GONE);
            this.progressLabel.setVisibility(View.GONE);
        } else {
            this.progressBarFloating.setVisibility(View.GONE);
            if (this.isCancelButtonHidden && this.isInfoButtonHidden) {
                this.actionButtonGroup.setVisibility(View.GONE);
            }
        }
    }

    protected void getAttributes(@Nullable final AttributeSet attrs) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, R.styleable.StepHeader);
        this.isInfoButtonHidden = a.getBoolean(R.styleable.StepHeader_isInfoButtonHidden, false);
        this.isProgressBarHidden = a.getBoolean(R.styleable.StepHeader_isProgressBarHidden, false);
        this.isProgressLabelHidden = a.getBoolean(R.styleable.StepHeader_isProgressLabelHidden, false);
        this.isCancelButtonHidden = a.getBoolean(R.styleable.StepHeader_isCancelButtonHidden, false);
        this.usesFloatingProgressBar = a.getBoolean(R.styleable.StepHeader_usesFloatingProgressBar, false);
        a.recycle();
    }
}
