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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public interface RecorderConfiguration extends AsyncActionConfiguration {
    /**
     * Returns the type of recorder that this config is for.
     *
     * @Return the type of recorder that this config is for.
     */
    @Override
    @NonNull
    @RecorderType
    String getType();

    /**
     * An identifier marking the step at which to stop the asyncAction. If `nil`, then the asyncAction will be stopped
     * when the task is stopped. Note: this bound is inclusive so the step with the stopStepIdentifier will have the recorder
     * running while it is executed.
     *
     * @return step identifier, or null
     */
    @Nullable
    String getStopStepIdentifier();

    /**
     * Returns a RecorderConfiguration identical to this except with the given stop step identifier.
     * @param stopStepIdentifier The new stopStepIdentifier to copy with.
     * @return a RecorderConfiguration identical to this except with the given step stop identifier.
     */
    @NonNull
    RecorderConfiguration copyWithStopStepIdentifier(@Nullable String stopStepIdentifier);
}
