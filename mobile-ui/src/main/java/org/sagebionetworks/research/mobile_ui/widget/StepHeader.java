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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.mobile_ui.R2;

import butterknife.BindView;

public class StepHeader extends ConstraintLayout {
    @BindView(R2.id.rs2_step_header_image_view)
    @NonNull
    protected ImageView imageView;
    @BindView(R2.id.rs2_step_header_cancel_button)
    @NonNull
    protected ImageButton cancelButton;
    @BindView(R2.id.rs2_step_header_progress_label)
    @NonNull
    protected TextView progressLabel;
    @BindView(R2.id.rs2_step_header_progress_bar)
    @NonNull
    protected ProgressBar progressBar;
    @BindView(R2.id.rs2_step_header_title_label)
    @NonNull
    protected TextView titleLabel;
    @BindView(R2.id.rs2_step_header_text_label)
    @NonNull
    protected TextView textLabel;

    public StepHeader(final Context context) {
        super(context);
        this.commonInit(null, 0);
    }

    public StepHeader(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.commonInit(attrs, 0);
    }

    public StepHeader(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.commonInit(attrs, defStyleAttr);
    }

    protected void commonInit(@Nullable final AttributeSet attrs, final int defStyleAttr) {
        inflate(this.getContext(), R.layout.rs2_step_header, this);
        this.getAttributes(attrs, defStyleAttr);
    }

    protected void getAttributes(@Nullable final AttributeSet attrs, final int defStyleAttr) {

    }

    @NonNull
    public ImageView getImageView() {
        return this.imageView;
    }

    @NonNull
    public ImageButton getCancelButton() {
        return this.cancelButton;
    }

    @NonNull
    public TextView getProgressLabel() {
        return this.progressLabel;
    }

    @NonNull
    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    @NonNull
    public TextView getTitleLabel() {
        return this.titleLabel;
    }

    @NonNull
    public TextView getTextLabel() {
        return this.textLabel;
    }

}
