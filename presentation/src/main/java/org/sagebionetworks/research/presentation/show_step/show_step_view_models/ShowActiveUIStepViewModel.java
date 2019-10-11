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

package org.sagebionetworks.research.presentation.show_step.show_step_view_models;

import androidx.lifecycle.LiveData;

import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;

public class ShowActiveUIStepViewModel<S extends ActiveUIStepView> extends ShowUIStepViewModel<S> {

    private ShowActiveUiStepViewModelHelper mModelHelper;

    /**
     * @return true if countdown is currently running, false if not running or paused.
     */
    public boolean isCountdownRunning() {
        return mModelHelper.isCountdownRunning();
    }

    /**
     * @return true if the countdown is currently paused, false otherwise.
     */
    public boolean isCountdownPaused() {
        return mModelHelper.isCountdownPaused();
    }

    public ShowActiveUIStepViewModel(final PerformTaskViewModel performTaskViewModel, final S stepView) {
        super(performTaskViewModel, stepView);
        mModelHelper = performTaskViewModel.getStepViewModelHelper(stepView.getIdentifier());
    }

    /**
     * This function starts the countdown from a value equal to the step view's provided duration,
     * and counts down at second intervals.
     * To get a countdown update every second, observe countdown LiveData.
     */
    public void startCountdown() {
        mModelHelper.startCountdown();
    }

    /**
     * This function pauses the countdown at its current countdown value.
     */
    public void pauseCountdown() {
        mModelHelper.pauseCountdown();
    }

    /**
     * This resumes the countdown from whatever its countdown value was when pauseCountdown() was called.
     * If pauseCountdown() was never called, nothing is done.
     */
    public void resumeCountdown() {
        mModelHelper.resumeCountdown();
    }

    public LiveData<Long> getCountdown() {
        return this.mModelHelper.countdown;
    }

    @Override
    protected void onCleared() {
        mModelHelper.cleanup();
        super.onCleared();
    }




}