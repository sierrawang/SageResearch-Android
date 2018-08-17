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

package org.sagebionetworks.research.mobile_ui.recorder;

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * A Recorder records some sort of data about the user (e.g. phone's motion, audio, etc). Recorders are typically
 * run on a different thread than ui so implementations should be thread safe to ensure there are no concurrency
 * issues.
 */
public interface Recorder extends Serializable {
    /**
     * Returns an identifier for this recorder that is unique among the recorders in it's task.
     * @return an identifier for this recorder that is unique among the recorders in it's task.
     */
    String getIdentifier();

    /**
     * Starts this recorder. This method should notify the recorder to start and then return immediately without
     * blocking or performing the recording on the UI thread. In most cases this means the recorder will need to
     * manage it's own threading.
     */
    void start();

    /**
     * Indicates that this recorder is done recording and should save it's results. This method should notify
     * the recorder to stop and then return immediately without blocking.
     */
    void stop();

    /**
     * Indicates that this recorder should stop recording and should discard it's results. This method should
     * notify the recorder to cancel and then return immediately without blocking.
     */
    void cancel();

    /**
     * Returns `true` if this recorder is currently recording, and `false` otherwise.
     * @return `true` if this recorder is currently recording, and `false` otherwise.
     */
    boolean isRecording();

    /**
     * Returns the identifier of the step to start the recorder on. If this method returns null the recorder will
     * start when the task is started.
     * @return the identifier of the step to start the recorder on.
     */
    @Nullable
    String getStartStepIdentifier();

    /**
     * Returns the identifier of the step to stop the recorder on. If this method returns null the recorder will
     * stop when the task is stopped.
     * @return the identifier of the step to start the recorder on.
     */
    @Nullable
    String getStopStepIdentifier();
}
