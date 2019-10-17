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

import androidx.annotation.Keep;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.mobile_ui.show_step.ShowStepContract.Presenter;
import org.sagebionetworks.research.mobile_ui.show_step.ShowStepContract.View;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Keep
public class StepPresenter implements Presenter<View> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepPresenter.class);

    private final PerformTaskViewModel performTaskViewModel;

    public StepPresenter(PerformTaskViewModel performTaskViewModel) {
        this.performTaskViewModel = performTaskViewModel;
    }

    @Override
    public void attachView(View view) {
        LOGGER.debug("attachView called");
    }

    @Override
    public void detachView() {
        LOGGER.debug("detachView called");
    }

    @Override
    public void finish() {
        LOGGER.debug("finish called");
    }

    @Override
    public void handleAction(final String actionType) {
        LOGGER.debug("handleAction called with actionType: {}", actionType);
    }

    @Override
    public void saveStepResult(final Result result) {
        LOGGER.debug("saveStepResult called with result: {}", result);
    }
}
