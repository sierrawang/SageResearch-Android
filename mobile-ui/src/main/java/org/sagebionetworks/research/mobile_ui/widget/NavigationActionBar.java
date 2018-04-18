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
import android.graphics.Canvas;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.mobile_ui.R2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * TODO: document your custom view class.
 */
@Keep
public class NavigationActionBar extends ConstraintLayout {
    @Keep
    public interface ActionButtonClickListener {
        void onClick(ActionButton actionButton);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationActionBar.class);

    private ActionButtonClickListener actionButtonClickListener;

    private Unbinder unbinder;

    public NavigationActionBar(Context context) {
        this(context, null);
    }

    public NavigationActionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.attr.navigationActionBar);
        init(attrs, R.attr.navigationActionBar, R.style.Widget_ResearchStack_NavigationActionBar);
    }

    public NavigationActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, R.style.Widget_ResearchStack_NavigationActionBar);
    }

    @Optional
    @OnClick({
            R2.id.rs2_step_navigation_action_add_more,
            R2.id.rs2_step_navigation_action_backward,
            R2.id.rs2_step_navigation_action_cancel,
            R2.id.rs2_step_navigation_action_forward,
            R2.id.rs2_step_navigation_action_learn_more,
            R2.id.rs2_step_navigation_action_skip
    })
    public void onActionButtonClick(ActionButton actionButton) {
        LOGGER.debug("Action button clicked, text: {}", actionButton.getText());

        if (actionButtonClickListener != null) {
            actionButtonClickListener.onClick(actionButton);
        }
    }

    public void setActionButtonClickListener(
            final ActionButtonClickListener actionButtonClickListener) {
        this.actionButtonClickListener = actionButtonClickListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbinder.unbind();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NavigationActionBar, defStyleAttr, 0);
        inflate(getContext(), R.layout.rs2_navigation_action_bar, this);
        a.recycle();
    }
}
