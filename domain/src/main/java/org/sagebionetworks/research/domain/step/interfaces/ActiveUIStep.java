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

package org.sagebionetworks.research.domain.step.interfaces;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * An ActiveUIStep is a step which asks the user to perform a task. Typically they have a duration which defines how
 * long the user should perform the task for. ActiveUISteps also have spoken instructions that can be read to the
 * user.
 */
public interface ActiveUIStep extends ThemedUIStep {
    /**
     * The set of commands to apply to this active step. These indicate actions to fire at the beginning and end of
     * the step such as playing a sound as well as whether or not to automatically start and finish the step.
     *
     * @return set of commands associated with this step
     */
    @Nullable
    ImmutableSet<String> getCommands();

    /**
     * The duration of time in seconds to run the step. If null, then this value is ignored.
     *
     * @return step duration in seconds
     */
    @Nullable
    Double getDuration();

    /**
     * Localized text that represents an instructional voice prompt. Instructional speech begins when the step passes
     * the time indicated by the given time.  If `timeInterval` is greater than or equal to `duration` or is equal to
     * `Double.infinity`, then the spoken instruction returned should be for when the step is finished. - parameter
     * timeInterval: The time interval at which to speak the instruction. - returns: The localized instruction to
     * speak or `nil` if there isn't an instruction. spokenInstruction(at timeInterval: TimeInterval) -> String?
     */
    @Nullable
    ImmutableMap<String, String> getSpokenInstructions();

    /**
     * Whether or not the step uses audio, such as the speech synthesizer, that should play whether or not the user
     * has the mute switch turned on.
     *
     * @return whether the step requires background audio
     */
    boolean isBackgroundAudioRequired();
}