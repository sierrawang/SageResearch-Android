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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.ActiveUIStepViewBinding;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;
import org.sagebionetworks.research.presentation.speech.TextToSpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ShowActiveUIStepFragmentBase<S extends ActiveUIStepView, VM extends ShowActiveUIStepViewModel<S>,
        SB extends ActiveUIStepViewBinding<S>> extends
        ShowUIStepFragmentBase<S, VM, SB> {
    private class Connection implements ServiceConnection {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
            isBound = true;
            textToSpeechService = ((TextToSpeechService.Binder)iBinder).getService();
            textToSpeechService.registerSpeechesOnCountdown(stepView.getDuration(), showStepViewModel.getCountdown(),
                    stepView.getSpokenInstructions());
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            isBound = false;
            textToSpeechService = null;
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ShowActiveUIStepFragmentBase.class);
    // Multiply integer animation values by a constant to smooth it out.
    public static final int PROGRESS_BAR_ANIMATION_MULTIPLIER = 100;

    private Connection connection;
    private boolean isBound;
    private TextToSpeechService textToSpeechService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = new Connection();
        if (!stepView.getSpokenInstructions().isEmpty()) {
            bindTextToSpeechService();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, viewGroup, savedInstanceState);
        Long duration = this.stepView.getDuration().getSeconds();
        ProgressBar countdownDial = this.stepViewBinding.getCountdownDial();
        if (countdownDial != null) {
            countdownDial.setProgress(0);
            countdownDial.setMax(duration.intValue() * PROGRESS_BAR_ANIMATION_MULTIPLIER);
        }

        TextView countLabel = this.stepViewBinding.getCountLabel();
        if (countLabel != null) {
            countLabel.setText(duration.toString());
        }

        Observer<Long> countdownObserver = this.getCountdownObserver();
        if (countdownObserver != null) {
            LiveData<Long> countdown = showStepViewModel.getCountdown();
            countdown.observe(this, this.getCountdownObserver());
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!stepView.getSpokenInstructions().isEmpty() && !isBound) {
            LOGGER.warn("TextToSpeechService is not bound");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBound) {
            getContext().unbindService(connection);
        }
    }


    /**
     * Returns The observer to use on the countdown. By default the observer sets the count label to
     * the current value of the countdown, or null if no observer should be set.
     * @return The observer to use on the countdown or null if no observer should be set.
     */
    @Nullable
    protected Observer<Long> getCountdownObserver() {
        return count -> {
            if (count == null) {
                return;
            }

            if (count == 0) {
                // TODO rkolmos 07/24/2018 implement commands and fix this to not always go forward
                showStepViewModel.handleAction(ActionType.FORWARD);
                return;
            }

            Integer duration = ((Long)this.stepView.getDuration().getSeconds()).intValue();
            int from = (int)(duration - count);
            Animator animator = this.getCountdownAnimator(from, from + 1);
            if (animator !=null) {
                animator.start();
            }

            TextView countLabel = this.stepViewBinding.getCountLabel();
            if (countLabel != null) {
                countLabel.setText(String.format(getResources().getConfiguration().locale,"%d",count));
            }
        };
    }

    /**
     * Returns the Animator to use to animate the countdown. By default the animate animates the
     * Countdown dial to progress from from to to over the duration to - from where to and from are the provided
     * integers. Can return null if no animation should be used.
     * @param from The progress to animate from.
     * @param to The progress to animate to.
     * @return the Animator to use to animate the countdown or null if no animation should be used.
     */
    @Nullable
    protected Animator getCountdownAnimator(int from, int to) {
        ProgressBar countdownDial = this.stepViewBinding.getCountdownDial();
        if (countdownDial != null) {
            ObjectAnimator animator = ObjectAnimator.ofInt(countdownDial, "progress",
                   from * PROGRESS_BAR_ANIMATION_MULTIPLIER, to * PROGRESS_BAR_ANIMATION_MULTIPLIER);
            animator.setDuration((to - from) * 1000);
            animator.setStartDelay(0);
            animator.setInterpolator(new LinearInterpolator());
            return animator;
        }

        return null;
    }

    protected void bindTextToSpeechService() {
        Intent intent = new Intent(getContext(), TextToSpeechService.class);
        getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
}
