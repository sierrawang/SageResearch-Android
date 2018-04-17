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

package org.sagebionetworks.research.domain.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import org.sagebionetworks.research.domain.mobile.ui.R;
import org.sagebionetworks.research.presentation.model.StepView.NavDirection;

/**
 * Base class for a {@link FrameLayout} container that will perform animations when switching between two steps. There
 * will, at most, be two steps when animating. The step going off screen will eventually be removed.
 */
public class StepSwitcher extends FrameLayout {
    public static final DecelerateInterpolator interpolator = new DecelerateInterpolator(2);

    private int animationTime;

    /**
     * Creates a new empty StepSwitcher.
     *
     * @param context
     *         the application's environment
     */
    public StepSwitcher(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new empty StepSwitcher for the given context and with the specified set attributes.
     *
     * @param context
     *         the application environment
     * @param attrs
     *         a collection of attributes
     */
    public StepSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Creates a new empty StepSwitcher for the given context and with the specified set attributes.
     *
     * @param context
     *         the application environment
     * @param attrs
     *         a collection of attributes
     * @param defStyleAttr
     *         An attribute in the current theme that contains a reference to a style resource that supplies defaults
     *         values for the TypedArray.  Can be 0 to not look for defaults.
     */
    public StepSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        animationTime = getResources().getInteger(R.integer.rs2_medium_anim_duration_ms);
    }

    /**
     * Adds a new step to the view hierarchy. If a step is currently showing, the direction parameter is used to
     * indicate which direction(x-axis) that the views should animate to.
     *
     * @param newStep
     *         the step you want to switch to
     * @param direction
     *         the direction of the animation in the x direction. This values can either be {@link
     *         NavDirection#SHIFT_LEFT} or {@link NavDirection#SHIFT_RIGHT}
     */
    public void show(@NonNull View newStep, @NavDirection int direction) {
        show(newStep, direction, false);
    }

    /**
     * Adds a new step to the view hierarchy. If a step is currently showing, the direction parameter is used to
     * indicate which direction(x-axis) that the views should animate to.
     *
     * @param newStep
     *         the step you want to switch to
     * @param direction
     *         the direction of the animation in the x direction. This values can either be {@link
     *         NavDirection#SHIFT_LEFT} or {@link NavDirection#SHIFT_RIGHT}
     * @param alwaysReplaceView
     *         if true, even if the view have the same step id, they will be replaced useful if you are trying to
     *         refresh a step view with different UI state
     */
    public void show(@NonNull View newStep, int direction, boolean alwaysReplaceView) {
        // if layouts originate from the same step, ignore show
        View currentStep = findViewById(R.id.rs2_current_step);
        if (currentStep != null) {
            String currentStepId = (String) currentStep.getTag(R.id.rs2_step_id);
            String stepLayoutId = (String) newStep.getTag(R.id.rs2_step_id);
            if (currentStepId.equals(stepLayoutId) && !alwaysReplaceView) {
                return;
            }
        }

        // Force crash when invalid direction is passed in. The values of the constants are used
        // when calculating the x-traversal distance
        if (direction != NavDirection.SHIFT_LEFT && direction != NavDirection.SHIFT_RIGHT) {
            throw new IllegalArgumentException(
                    "NavDirection with value: " + direction + " is not supported.");
        }

        post(() -> {
            // Set the id of current as something other than R.id.current_step
            int currentIndex = 0;
            if (currentStep != null) {
                currentStep.setId(0);
                currentIndex = indexOfChild(currentStep);
            }

            // Add the new step to the view stack & set the id as the current step. Set the index
            // in the view hierarchy as the same as the current step on-screen
            LayoutParams lp = getLayoutParams(newStep);
            addView(newStep, currentIndex, lp);
            newStep.setId(R.id.rs2_current_step);

            // If the old step is gone, we can go ahead and ignore the following animation code.
            // This will usually happen on attachView-up of the host (e.g. activity)
            if (currentStep != null) {
                int newTranslationX = direction * getWidth();

                newStep.setTranslationX(newTranslationX);
                newStep.animate()
                        .setDuration(animationTime)
                        .setInterpolator(interpolator)
                        .translationX(0);

                currentStep.animate()
                        .setInterpolator(interpolator)
                        .setDuration(animationTime)
                        .translationX(-1 * newTranslationX)
                        .withEndAction(() -> {
                            removeView(currentStep);
                        });
            }
        });
    }

    private LayoutParams getLayoutParams(View stepView) {
        LayoutParams lp = (LayoutParams) stepView.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        return lp;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return StepSwitcher.class.getName();
    }

}
