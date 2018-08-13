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

package org.sagebionetworks.research.presentation.perform_task.active.async.runner;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.presentation.perform_task.active.async.StepChangeListener;

/**
 * Created by liujoshua on 10/11/2017.
 */

public abstract class AsyncActionRunner implements StepChangeListener {
    private final AsyncActionConfiguration asyncActionConfiguration;

    public AsyncActionRunner(@NonNull AsyncActionConfiguration asyncActionConfiguration) {
        checkNotNull(asyncActionConfiguration);
        this.asyncActionConfiguration = asyncActionConfiguration;
    }

    @Override
    @CallSuper
    public void onCancelStep(Step step) {
        //no-op
    }

    @Override
    @CallSuper
    public void onFinishStep(Step step) {
        //no-op
    }

    @Override
    @CallSuper
    public void onShowStep(Step step) {
        if (step.getIdentifier().equals(this.asyncActionConfiguration.getStartStepIdentifier())) {
            runAction();
        }
        // TODO: run async actions without a startStepIdentifier for first step, e.g. introduce a AsyncAction
        // model for presentation layer that defaults in startStepIdentifier
    }

    protected abstract void runAction();
}
