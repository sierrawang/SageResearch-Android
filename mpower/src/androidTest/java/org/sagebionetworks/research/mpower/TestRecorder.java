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

package org.sagebionetworks.research.mpower;

import android.support.annotation.Nullable;

import org.sagebionetworks.research.mobile_ui.recorder.Recorder;

/**
 * A generic implementation of Recorder to facilitate testing the recorder service.
 */
public class TestRecorder implements Recorder {
    private boolean startCalled;
    private boolean stopCalled;
    private boolean cancelCalled;
    private final String startStepIdentifier;
    private final String stopStepIdentifier;

    public TestRecorder(final String startStepIdentifier, final String stopStepIdentifier) {
        this.startStepIdentifier = startStepIdentifier;
        this.stopStepIdentifier = stopStepIdentifier;
        this.startCalled = false;
        this.stopCalled = false;
        this.cancelCalled = false;
    }

    @Override
    public void start() {
        this.startCalled = true;
        this.stopCalled = false;
        this.cancelCalled = false;
    }

    @Override
    public void stop() {
        this.startCalled = false;
        this.stopCalled = true;
        this.cancelCalled = false;
    }

    @Override
    public void cancel() {
        this.startCalled = false;
        this.stopCalled = false;
        this.cancelCalled = true;
    }

    @Override
    public boolean isRunning() {
        return this.startCalled;
    }

    public boolean isStartCalled() {
        return startCalled;
    }

    public boolean isStopCalled() {
        return stopCalled;
    }

    public boolean isCancelCalled() {
        return cancelCalled;
    }

    @Nullable
    @Override
    public String getStartStepIdentifier() {
        return this.startStepIdentifier;
    }

    @Nullable
    @Override
    public String getStopStepIdentifier() {
        return this.stopStepIdentifier;
    }
}
