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
import android.graphics.PorterDuff.Mode;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.mobile_ui.R2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * TODO: document your custom view class.
 */
@Keep
public class NavigationActionBar extends ConstraintLayout {
//    @Keep
//    public interface ActionButtonClickListener {
//        void onClick(ActionButton actionButton);
//    }
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationActionBar.class);

//    @BindView(R2.id.rs2_step_navigation_action_backward)
//    @Nullable
//    ActionButton backwardButton;
//
//    @BindView(R2.id.rs2_step_navigation_action_forward)
//    @Nullable
//    ActionButton forwardButton;
//
//    @BindView(R2.id.rs2_step_navigation_action_skip)
//    @Nullable
//    ActionButton skipButton;
//
//    @BindView(R2.id.rs2_step_navigation_shadow_view)
//    @Nullable
//    View shadowView;

    private boolean isBackwardHidden;
    private boolean isForwardHidden;
    private boolean isShadowHidden;
    private boolean isSkipHidden;
    private int primaryActionColor;
    private int primaryActionTitleColor;
    private int skipActionColor;
    //private ActionButtonClickListener actionButtonClickListener;
    private Unbinder unbinder;

    public NavigationActionBar(Context context) {
        this(context, null);
    }

    public NavigationActionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(attrs, 0);
    }

    public NavigationActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

//    @Optional
//    @OnClick({
//            R2.id.rs2_step_navigation_action_backward,
//            R2.id.rs2_step_navigation_action_forward,
//            R2.id.rs2_step_navigation_action_skip
//    })
//    public void onActionButtonClick(ActionButton actionButton) {
//        LOGGER.debug("Action button clicked, text: {}", actionButton.getText());
//
//        if (actionButtonClickListener != null) {
//            actionButtonClickListener.onClick(actionButton);
//        }
//    }
//
//    public void setActionButtonClickListener(
//            final ActionButtonClickListener actionButtonClickListener) {
//        this.actionButtonClickListener = actionButtonClickListener;
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unbinder.unbind();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!this.isInEditMode()) {
            this.unbinder = ButterKnife.bind(this);
        } else {
//            this.backwardButton = this.findViewById(R.id.rs2_step_navigation_action_backward);
//            this.forwardButton = this.findViewById(R.id.rs2_step_navigation_action_forward);
//            this.skipButton = this.findViewById(R.id.rs2_step_navigation_action_skip);
//            this.shadowView = this.findViewById(R.id.rs2_step_navigation_shadow_view);
        }
        //this.layoutComponents();
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        this.getAttributes(attrs, defStyleAttr);
        inflate(this.getContext(), R.layout.rs2_navigation_action_bar, this);
    }

    protected void getAttributes(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NavigationActionBar, defStyleAttr, 0);
        this.isForwardHidden = a.getBoolean(R.styleable.NavigationActionBar_isForwardHidden, false);
        this.isBackwardHidden = a.getBoolean(R.styleable.NavigationActionBar_isBackwardHidden, false);
        this.isShadowHidden = a.getBoolean(R.styleable.NavigationActionBar_isShadowHidden, false);
        this.isSkipHidden = a.getBoolean(R.styleable.NavigationActionBar_isSkipHidden, false);
        this.primaryActionColor = a.getColor(R.styleable.NavigationActionBar_primaryActionColor, 0);
        this.primaryActionTitleColor = a.getColor(R.styleable.NavigationActionBar_primaryActionTitleColor, 0);
        this.skipActionColor = a.getColor(R.styleable.NavigationActionBar_skipActionColor, 0);
        a.recycle();
    }

    protected void layoutComponents() {
//        hideComponentIfNecessary(this.forwardButton, this.isForwardHidden);
//        hideComponentIfNecessary(this.backwardButton, this.isBackwardHidden);
//        hideComponentIfNecessary(this.skipButton, this.isSkipHidden);
//        hideComponentIfNecessary(this.shadowView, this.isShadowHidden);
//
//        if (this.primaryActionColor != 0) {
//            this.forwardButton.getBackground().setColorFilter(this.primaryActionColor, Mode.SRC_IN);
//            this.backwardButton.getBackground().setColorFilter(this.primaryActionColor, Mode.SRC_IN);
//        }
//
//        if (this.primaryActionTitleColor != 0) {
//            this.forwardButton.setTextColor(this.primaryActionTitleColor);
//            this.backwardButton.setTextColor(this.primaryActionTitleColor);
//        }
//
//        if (this.skipActionColor != 0) {
//            this.skipButton.getBackground().setColorFilter(this.skipActionColor, Mode.SRC_IN);
//        }
    }

    private static void hideComponentIfNecessary(View component, boolean isHidden) {
        if (component != null && isHidden) {
            component.setVisibility(View.GONE);
        }
    }
}
