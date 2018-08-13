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

package org.sagebionetworks.research.domain.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Defines general configuration for asynchronous asyncAction that should be run in the background. Depending upon the
 * parameters and how the asyncAction is setup, this could be something that is run continuously or else is paused or
 * reset based on a timeout interval.
 */
public interface AsyncActionConfiguration {
    /**
     * A short string that uniquely identifies the asyncronous asyncAction within the task. The identifier is
     * reproduced in the results of a async results.
     *
     * @return identifier
     */
    @NonNull
    String getIdentifier();

    /**
     * Returns the start step identifier for this AsyncActionConfiguration. Note: The identifier is inclusive so
     * the step with the given identifier will have the recorder running while it is active.
     * @return step identifier, or null
     */
    @Nullable
    String getStartStepIdentifier();

    /**
     * An identifier marking the step to start the asyncAction. If `null`, then the asyncAction will be started when
     * the task is started.
     */
    @NonNull
    String getType();

    /**
     * Returns an AsyncActionConfiguration identical to this one with the given start step identifier
     * @param startStepIdentifier The new startStepIdentifier to copy with.
     * @return an AsyncActionConfiguration identical to this one with the given start step identifier
     */
    @NonNull
    AsyncActionConfiguration copyWithStartStepIdentifier(@Nullable String startStepIdentifier);
}
