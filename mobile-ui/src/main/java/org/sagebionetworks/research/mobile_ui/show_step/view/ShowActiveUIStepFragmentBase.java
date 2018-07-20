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

package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.LiveData;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.ActiveUIStepViewBinding;
import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;

public abstract class ShowActiveUIStepFragmentBase<S extends ActiveUIStepView, VM extends ShowActiveUIStepViewModel<S>,
        SB extends ActiveUIStepViewBinding<S>> extends
        ShowUIStepFragmentBase<S, VM, SB> {
    public static final int PROGRESS_BAR_ANIMATION_MULTIPLIER = 1000;

    protected LiveData<Long> countdown;

    @Override
    public void onStart() {
        super.onStart();
        this.countdown = this.showStepViewModel.getCountdown();
        ProgressBar countdownDial = this.stepViewBinding.getCountdownDial();
        Integer duration = ((Long)this.stepView.getDuration().getSeconds()).intValue();
        if (countdownDial != null) {
            countdownDial.setMax(duration * PROGRESS_BAR_ANIMATION_MULTIPLIER);
            ObjectAnimator animator = ObjectAnimator.ofInt(countdownDial, "progress", 0,
                    duration * PROGRESS_BAR_ANIMATION_MULTIPLIER);
            animator.setDuration(duration * 1000);
            animator.setStartDelay(0);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }

        TextView countdownLabel = this.stepViewBinding.getCountdownLabel();
        if (countdownLabel != null) {
            countdownLabel.setText(duration.toString());
        }

        TextView unitLabel = this.stepViewBinding.getUnitLabel();
        this.countdown.observe(this, count -> {
            if (count == null) {
                return;
            }

            if (count == 0) {
                this.performTaskViewModel.goForward();
                return;
            }

            if (countdownLabel != null) {
                countdownLabel.setText(count.toString());
            }

            if (countdownDial != null) {
            }
        });
    }
}
