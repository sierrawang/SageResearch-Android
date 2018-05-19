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

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R2.id;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;

public class OverviewStepViewBinding extends UIStepViewBinding {
    @NonNull
    @BindViews({id.leftIconImageView, id.centerIconImageView, id.rightIconImageView})
    protected List<ImageView> iconImageViews;

    @NonNull
    @BindViews({id.leftIconLabel, id.centerIconLabel, id.rightIconLabel})
    protected List<TextView> iconLabels;

    @NonNull
    @BindView(id.iconDescriptionLabel)
    protected TextView iconDescriptionLabel;

    public OverviewStepViewBinding(final View view) {
        super(view);
    }

    /**
     * Returns the list of image views that serve to display the icons for this binding.
     * @return the list of image views that serve to display the icons for this binding.
     */
    @NonNull
    public List<ImageView> getIconImageViews(){
        return this.iconImageViews;
    }

    /**
     * Returns the list of icon labels that serve to display the descriptions of the icons for this binding.
     * @return the list of icon labels that serve to display the descriptions of the icons for this binding.
     */
    @NonNull
    public List<TextView> getIconLabels() {
        return this.iconLabels;
    }

    /**
     * Returns the label that serves to display the overall description of all of the icons for this binding.
     * @return the label that serves to display the overall description of all of the icons for this binding.
     */
    @NonNull
    public TextView getIconDescriptionLabel() {
        return this.iconDescriptionLabel;
    }
}
