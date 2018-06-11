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

package org.sagebionetworks.research.mpower.step_binding;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.mpower.R;
import org.sagebionetworks.research.mpower.step_view.IconView;
import org.sagebionetworks.research.mpower.step_view.OverviewStepView;
import org.sagebionetworks.research.presentation.DisplayString;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * An OverviewStepViewBinding is an extension of UIStepViewBinding that also has icon views. These icon views
 * consist of an image view which displays the icon and a label which displays a description of the icon. There are
 * three of these views left, right, and center which are filled in the order center, left, right, depending on how
 * many icons are present.
 * @param <S> The type of step view this binding expects it's update method to recieve.
 */
public class OverviewStepViewBinding<S extends OverviewStepView> extends UIStepViewBinding<S> {
    private final OverviewStepViewHolder overviewStepViewHolder;
    private final Unbinder overviewStepViewHolderUnbinder;

    public OverviewStepViewBinding(View view) {
        super(view);
        this.overviewStepViewHolder = new OverviewStepViewHolder();
        this.overviewStepViewHolderUnbinder = ButterKnife.bind(this.overviewStepViewHolder, view);
    }

    @Override
    public void unbind() {
        super.unbind();
        this.overviewStepViewHolderUnbinder.unbind();
    }

    public List<ImageView> getIconImageViews() {
        return this.overviewStepViewHolder.iconImageViews;
    }

    public List<TextView> getIconLabels() {
        return this.overviewStepViewHolder.iconLabels;
    }

    @Override
    public void update(S overviewStepView) {
        super.update(overviewStepView);
        List<ImageView> iconImageViews = this.getIconImageViews();
        List<TextView> iconLabels = this.getIconLabels();

        List<IconView> iconViews = overviewStepView.getIconViews();
        for (int i = 0; i < iconImageViews.size(); i++) {
            IconView iconView = null;
            if (iconViews.size() > i) {
                iconView = iconViews.get(i);
            }

            if (iconView == null) {
                iconImageViews.get(i).setVisibility(View.GONE);
                iconLabels.get(i).setVisibility(View.GONE);
            } else {
                // TODO rkolmos 06/06/2018 update image view
                DisplayString title = iconView.getTitle();
                if (title != null) {
                    String titleString = null;
                    if (title.displayString != null) {
                        titleString = title.displayString;
                    } else if (title.defaultDisplayStringRes != null) {
                        // TODO rkolmos 06/06/2018 resolve the resource and use it instead.
                    }

                    iconLabels.get(i).setText(titleString);
                }
            }
        }
    }

    protected static class OverviewStepViewHolder {
        @BindViews({R.id.centerIconImageView, R.id.leftIconImageView, R.id.rightIconImageView})
        public List<ImageView> iconImageViews;

        @BindViews({R.id.centerIconLabel, R.id.leftIconLabel, R.id.rightIconLabel})
        public List<TextView> iconLabels;
    }
}
