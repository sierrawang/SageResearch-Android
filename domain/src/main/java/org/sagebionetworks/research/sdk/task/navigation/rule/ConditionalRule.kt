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

package org.sagebionetworks.research.sdk.task.navigation.rule

import org.sagebionetworks.research.sdk.result.TaskResult
import org.sagebionetworks.research.sdk.step.Step

/**
 * A conditional rule is appended to the navigable task to check a secondary source for
 * whether or not the step should be displayed.
 */
interface ConditionalRule {

    /**
     * Asks the conditional rule what the identifier is for the next step to display after the given step is
     * displayed.
     *
     * @param step step about to be displayed
     * @param taskResults current task result
     * @return id of next step to be displayed
     */
    fun skipToStep(step: Step, taskResults: TaskResult?): String?

    /**
     * Asks the conditional rule what the identifier is for the next step to display after the given step is
     * displayed.
     *
     * @param step step that just finished
     * @param taskResults current task result
     * @return id of next step to be displayed
     */
    fun nextStepIdentifier(step: Step?, taskResults: TaskResult?): String?

    interface Replacement : ConditionalRule {

        /**
         * Allows conditional rule to mutate or replace step that the navigation rules have
         * determined should be the return step.
         *
         * @param step       step that navigation has opted to return
         * @param taskResult current task result
         * @return mutated/replaced step, or original step for no mutation or replacement
         */
        fun getReplacementStep(step: Step, taskResult: TaskResult?): Step?
    }
}