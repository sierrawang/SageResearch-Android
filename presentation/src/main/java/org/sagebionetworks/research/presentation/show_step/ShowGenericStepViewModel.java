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

package org.sagebionetworks.research.presentation.show_step;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowStepViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.concurrent.atomic.AtomicBoolean;

public class ShowGenericStepViewModel extends ShowStepViewModel<StepView> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowGenericStepViewModel.class);

    protected final PerformTaskViewModel performTaskViewModel;

    protected final MutableLiveData<StepView> showStepViewModelMutableLiveData;

    protected final StepView stepView;

    private final AtomicBoolean addedResult;

    private final Instant startTime;

    public ShowGenericStepViewModel(PerformTaskViewModel performTaskViewModel, StepView stepView) {
        this.performTaskViewModel = performTaskViewModel;
        this.stepView = stepView;

        showStepViewModelMutableLiveData = new MutableLiveData<>();
        showStepViewModelMutableLiveData.setValue(stepView);
        this.addedResult = new AtomicBoolean();
        this.startTime = Instant.now();
    }

    @Override
    public LiveData<StepView> getStepView() {
        return showStepViewModelMutableLiveData;
    }

    @Override
    public void handleAction(final String actionType) {
        LOGGER.debug("handleAction called with actionType: {}", actionType);
        switch (actionType) {
            case ActionType.FORWARD:
                if (!addedResult.get()) {
                    // If for whatever reason the step didn't create a result matching it's identifier we create a
                    // ResultBase to mark that the step completed.
                    addStepResult(new ResultBase(stepView.getIdentifier(), startTime, Instant.now()));
                }
                performTaskViewModel.goForward();
                break;
            case ActionType.BACKWARD:
                performTaskViewModel.goBack();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported actionType: " + actionType);
        }
    }

    protected void addStepResult(Result result) {
        addedResult.set(true);
        performTaskViewModel.addStepResult(result);
    }

    @Override
    protected void onCleared() {
        LOGGER.debug("onCleared called");
    }
}
