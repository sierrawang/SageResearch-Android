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

package org.sagebionetworks.research.mobile_ui.show_step;

import android.support.annotation.Nullable;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy;
import org.sagebionetworks.research.mobile_ui.show_step.ShowStepContract.View;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;

public class NavigationRuleStepPresenter extends StepPresenter
        implements StepNavigationStrategy.SkipStepStrategy, StepNavigationStrategy.NextStepStrategy,
        StepNavigationStrategy.BackStepStrategy {
    public NavigationRuleStepPresenter(final PerformTaskViewModel performTaskViewModel) {
        super(performTaskViewModel);
    }

    @Override
    public void attachView(View view) {

    }

    @Override
    public void finish() {

    }

    @Override
    public void handleAction(final String actionType) {

    }

    @Override
    public void saveStepResult(final Result result) {

    }

    @Nullable
    @Override
    public String getNextStepIdentifier(@Nullable final TaskResult taskResult) {
        return null;
    }

    @Override
    public boolean isBackAllowed(@Nullable final TaskResult taskResult) {
        return false;
    }

    @Override
    public boolean shouldSkip(@Nullable final TaskResult taskResult) {
        return false;
    }
}
