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
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.widget.RoundedButton;
import org.sagebionetworks.research.mobile_ui.widget.UnderlinedButton;

public class GenericNavigationFooterView extends ConstraintLayout {
    private RoundedButton nextButton;
    private RoundedButton backButton;
    private UnderlinedButton reminderButton;
    private View shadowView;
    private boolean isBackHidden;
    private boolean isReminderHidden;
    private boolean isShadowHidden;
    private int primaryButtonColor;
    private int secondaryButtonColor;
    private int primaryTextColor;

    public GenericNavigationFooterView(final Context context) {
        super(context);
    }

    public GenericNavigationFooterView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.commonInit(attrs, 0, 0);
    }

    public GenericNavigationFooterView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.commonInit(attrs, defStyleAttr, 0);
    }

    private void commonInit(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.inflate(this.getContext(), R.layout.mpower2_generic_step_footer, this);
        this.getAttributes(attrs, defStyleAttr, defStyleRes);
        this.getComponents();
        setupPrimaryButton(this.nextButton, false);
        setupPrimaryButton(this.backButton, this.isBackHidden);
        if (this.isReminderHidden) {
            this.reminderButton.setVisibility(View.GONE);
        } else {
            this.reminderButton.setTextColor(this.secondaryButtonColor);
        }

        if (this.isShadowHidden) {
            this.shadowView.setVisibility(View.GONE);
        }
    }

    private void getAttributes(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = this.getContext().obtainStyledAttributes(attrs, R.styleable.GenericNavigationFooterView,
                defStyleAttr, defStyleRes);
        this.isBackHidden = a.getBoolean(R.styleable.GenericNavigationFooterView_isBackHidden, false);
        this.isReminderHidden = a.getBoolean(R.styleable.GenericNavigationFooterView_isReminderHidden, false);
        this.isShadowHidden = a.getBoolean(R.styleable.GenericNavigationFooterView_isShadowHidden, false);
        this.primaryButtonColor= a.getInt(R.styleable.GenericNavigationFooterView_primaryButtonColor,
                R.color.butterscotch500);
        this.secondaryButtonColor = a.getInt(R.styleable.GenericNavigationFooterView_secondaryButtonColor,
                R.color.royal500);
        this.primaryTextColor = a.getInt(R.styleable.GenericNavigationFooterView_primaryButtonTextColor,
                R.color.appTextDark);
        a.recycle();
    }

    private void getComponents() {
        ConstraintLayout root = this.findViewById(R.id.layout);
        this.nextButton = (RoundedButton) root.getViewById(R.id.footer_action_forward);
        this.backButton = (RoundedButton) root.getViewById(R.id.footer_action_backward);
        this.reminderButton = (UnderlinedButton) root.getViewById(R.id.footer_action_reminder);
        this.shadowView = root.getViewById(R.id.footer_shadow);
    }

    private void setupPrimaryButton(RoundedButton button, boolean isHidden) {
        if (isHidden) {
            button.setVisibility(View.GONE);
        } else {
            button.getBackground().setColorFilter(this.primaryButtonColor, Mode.SRC_IN);
            button.setTextColor(this.primaryTextColor);
        }
    }
}
