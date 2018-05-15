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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.sagebionetworks.research.domain.mobile_ui.R;

public class ProgressToolbar extends Toolbar {
    private static final int PROGRESS_BAR_ANIM_DURATION = 200; // ms
    private static final int PROGRESS_BAR_SCALE_FACTOR = 1000; // allows smooth animation.
    private ProgressBar progressBar;

    // TODO get this color right.
    private @ColorRes int defaultStatusBarColor = R.color.primaryTint;
    public void setDefaultStatusBarColor(@ColorRes int colorRes) {
        this.defaultStatusBarColor = colorRes;
    }

    // TODO get this color right.
    private @ColorRes int defaultTintColor = R.color.appWhite;
    public void setDefaultTintColor(@ColorRes int colorRes) {
        this.defaultTintColor = colorRes;
    }

    // TODO get this color right.
    private @ColorRes int defaultStepProgressColor = R.color.appWhite;
    public void setDefaultStepProgressColor(@ColorRes int colorRes) {
        this.defaultStepProgressColor = colorRes;
    }

    public ProgressToolbar(final Context context) {
        super(context);
        this.commonInit();
    }

    public ProgressToolbar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.commonInit();
    }

    public ProgressToolbar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.commonInit();
    }

    private void commonInit() {
        this.progressBar = new ProgressBar(this.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        this.progressBar.setIndeterminate(false);
        this.progressBar.setVisibility(View.INVISIBLE);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_bar));
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(progressBar, params);
    }

    public void setTint(@ColorRes int color) {
        int colorRes = ContextCompat.getColor(getContext(), color);
        Drawable drawable = getNavigationIcon();
        if (drawable != null) {
            drawable.setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
        }
        for (int i = 0; i < getMenu().size(); i++) {
            MenuItem menuItem = getMenu().getItem(i);
            if (menuItem != null && menuItem.getIcon() != null) {
                menuItem.getIcon().setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void setProgress(int progress, int max) {
        int scaledFrom = progressBar.getProgress();
        int scaledTo = progress * PROGRESS_BAR_SCALE_FACTOR;
        int scaledMax = max * PROGRESS_BAR_SCALE_FACTOR;

        // if the max changed we can't safely animate the change
        boolean animate = (progressBar.getMax() == scaledMax);

        if (animate) {
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, scaledFrom, scaledTo);
            anim.setDuration(PROGRESS_BAR_ANIM_DURATION);
            progressBar.startAnimation(anim);
        } else {
            progressBar.setMax(scaledMax);
            progressBar.setProgress(scaledTo);
        }
    }

    public void showProgressInToolbar(boolean showProgress) {
        // Hide with INVISIBLE so that the "Step 1 of 4" title does not show automatically
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
    }
}
