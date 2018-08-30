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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

import org.sagebionetworks.research.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.R2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * A NavigationActionBar contains the standard navigation action views (backward, forward, skip, and a footer shadow).
 * It allows these buttons to be hidden and lays the buttons out in a standardized way depending on which buttons are
 * present. It also stores and forwards button presses to an ActionButtonClickListener.
 * <p>
 * Note: The primary buttons are considered to be the forward and backward buttons, By default the shadow is laid out
 * at the top of the NavigationActionBar, below it are the primary buttons spaced out evenly depending on what is
 * present, backward then forward, left to right. Below the primary buttons the skip button is centered if present.
 */
@Keep
public class NavigationActionBar extends ConstraintLayout {
    /**
     * An ActionButtonClickListener decides what to do when an ActionButton is tap.
     */
    @Keep
    public interface ActionButtonClickListener {
        /**
         * Handles the event that the given actionButton was tapped.
         *
         * @param actionButton
         *         The action button that the user tapped.
         */
        void onClick(ActionButton actionButton);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationActionBar.class);

    @BindView(R2.id.rs2_step_navigation_action_backward)
    @NonNull
    ActionButton backwardButton;

    @BindView(R2.id.rs2_step_navigation_action_forward)
    @NonNull
    ActionButton forwardButton;

    @BindView(R2.id.rs2_step_navigation_primary_button_group)
    @NonNull
    ConstraintLayout primaryButtonGroup;

    @BindView(R2.id.rs2_step_navigation_shadow)
    @NonNull
    View shadowView;

    @BindView(R2.id.rs2_step_navigation_action_skip)
    @NonNull
    ActionButton skipButton;

    private ActionButtonClickListener actionButtonClickListener;

    private boolean isBackwardHidden;

    private boolean isForwardHidden;

    private boolean isShadowHidden;

    private boolean isSkipHidden;

    private int primaryActionColor;

    private int primaryActionTitleColor;

    private int skipActionColor;

    @NonNull
    private Unbinder unbinder;

    public NavigationActionBar(Context context) {
        this(context, null);
        commonInit(null, 0);
    }

    public NavigationActionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        commonInit(attrs, 0);
    }

    public NavigationActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit(attrs, defStyleAttr);
    }

    /**
     * Handles the event that the given ActionButton was clicked.
     *
     * @param actionButton
     *         The ActionButton that was clicked.
     */
    @Optional
    @OnClick({R2.id.rs2_step_navigation_action_backward, R2.id.rs2_step_navigation_action_forward,
            R2.id.rs2_step_navigation_action_skip})
    public void onActionButtonClick(@NonNull ActionButton actionButton) {
        LOGGER.debug("Action button clicked, text: {}", actionButton.getText());

        if (actionButtonClickListener != null) {
            actionButtonClickListener.onClick(actionButton);
        } else {
            LOGGER.debug("Action button clicked with null listener: {}", actionButton.getText());
        }
    }

    /**
     * Sets the ActionButtonClickListener to the given value.
     *
     * @param actionButtonClickListener
     *         The new ActionClickButtonListener to use with this object.
     */
    public void setActionButtonClickListener(@NonNull final ActionButtonClickListener actionButtonClickListener) {
        this.actionButtonClickListener = actionButtonClickListener;
    }

    /**
     * Helper method that performs the common initialization.
     *
     * @param attrs
     *         The attributed set to construct this object with or null if no such set was provided.
     * @param defStyleAttr
     *         The defStyleAttr provided.
     */
    protected void commonInit(@Nullable AttributeSet attrs, int defStyleAttr) {
        this.getAttributes(attrs, defStyleAttr);
        inflate(this.getContext(), R.layout.rs2_navigation_action_bar, this);
        // This call is necessary due to onFinishInflate not being called with manual inflation.
        this.onFinishInflate();
    }

    /**
     * Helper method which initializes the fields corresponding to the attributes from the given AttributedSet.
     *
     * @param attrs
     *         The set of attributes to get this objects fields from.
     * @param defStyleAttr
     *         The defStyleAttr provided.
     */
    protected void getAttributes(@Nullable AttributeSet attrs, int defStyleAttr) {
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

    /**
     * Helper method to hide and set colors according to the current state of this object.
     */
    protected void layoutComponents() {
        hideComponentIfNecessary(this.forwardButton, this.isForwardHidden);
        hideComponentIfNecessary(this.backwardButton, this.isBackwardHidden);
        // If both of the primary buttons are hidden we also hide the primary button group.
        hideComponentIfNecessary(this.primaryButtonGroup, this.isForwardHidden && this.isBackwardHidden);
        hideComponentIfNecessary(this.skipButton, this.isSkipHidden);
        hideComponentIfNecessary(this.shadowView, this.isShadowHidden);

        if (this.primaryActionColor != 0) {
            this.forwardButton.getBackground().setColorFilter(this.primaryActionColor, Mode.SRC_IN);
            this.backwardButton.getBackground().setColorFilter(this.primaryActionColor, Mode.SRC_IN);
        }

        if (this.primaryActionTitleColor != 0) {
            this.forwardButton.setTextColor(this.primaryActionTitleColor);
            this.backwardButton.setTextColor(this.primaryActionTitleColor);
        }

        if (this.skipActionColor != 0) {
            this.skipButton.setTextColor(this.skipActionColor);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unbinder.unbind();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.unbinder = ButterKnife.bind(this);
        this.layoutComponents();
    }

    /**
     * Hides the given view if the given boolean is true.
     *
     * @param component
     *         The view to potentially hide.
     * @param isHidden
     *         true if the view should be hidden false otherwise.
     */
    private static void hideComponentIfNecessary(View component, boolean isHidden) {
        if (component != null && isHidden) {
            component.setVisibility(View.GONE);
        }
    }
}
