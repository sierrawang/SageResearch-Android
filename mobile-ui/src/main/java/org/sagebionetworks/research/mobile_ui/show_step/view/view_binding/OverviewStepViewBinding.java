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
import android.widget.ImageView;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R2.id;

import butterknife.BindView;

/**
 * An OverviewStepViewBinding is a binding that has everything a UIStepViewBinding has and the following optional
 * fields
 *      - overallIconDescriptionLabel : TextView - The label to display a description of what all the icons mean
 *          (e.g. "This is what you'll need")
 *      - leftIconImageView : ImageView - The view to display the left icon on.
 *      - leftIconLabel : TextView - The label to display a description of the left icon on.
 *      - centerIconImageView : ImageView - The view to display the center icon on.
 *      - centerIconLabel : TextView - The label to display a description of the center icon on.
 *      - rightIconImageView : ImageView - The view to display the right icon on.
 *      - rightIconLabel : TextView - The label to display a description of the right icon on.
 */
public class OverviewStepViewBinding extends UIStepViewBinding {
    /**
     * The binding can optionally have a label which displays a description of the significance of the icons that
     * are displayed on the overview step.
     */
    @BindView(id.overallIconDescriptionLabel)
    @Nullable
    public TextView overallIconDescriptionLabel;

    /**
     * The binding can optionally have an image view which displays the left icon.
     */
    @BindView(id.leftIconImageView)
    @Nullable
    public ImageView leftIconImageView;

    /**
     * The binding can optionally have a label which displays a description specific to just the left icon.
     */
    @BindView(id.leftIconLabel)
    @Nullable
    public TextView leftIconLabel;

    /**
     * The binding can optionally have an image view which displays the center icon.
     */
    @BindView(id.centerIconImageView)
    @Nullable
    public ImageView centerIconImageView;

    /**
     * The binding can optionally have a label which displays a description specific to just the center icon.
     */
    @BindView(id.centerIconLabel)
    @Nullable
    public TextView centerIconLabel;

    /**
     * The binding can optionally have an image view which displays the right icon.
     */
    @BindView(id.rightIconImageView)
    @Nullable
    public ImageView rightIconImageView;

    /**
     * The binding can optionally have a label which displays a description specific to just the right icon.
     */
    @BindView(id.rightIconLabel)
    @Nullable
    public TextView rightIconLabel;

    // TODO rkolmos 05/25/2018 override update to do the correct thing once the corresponding subclass of StepView is created
}
