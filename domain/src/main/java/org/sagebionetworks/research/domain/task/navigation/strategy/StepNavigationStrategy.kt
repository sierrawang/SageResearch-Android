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

package org.sagebionetworks.research.domain.task.navigation.strategy

import org.sagebionetworks.research.domain.result.interfaces.TaskResult
import org.sagebionetworks.research.domain.task.Task

/**
 * Navigation rules that can be implemented by a step, which will be used by the conditional step navigator.
 */
class StepNavigationStrategy {

    interface NextStepStrategy {

        enum class Identifiers constructor(val key: String) {
            Exit("exit"), NextStep("nextStep"), NextSection("nextSection");
        }

        /**
         * NextStepStrategy step to navigate to based on the current task result.
         *
         * @param taskResult current task result
         * @return identifier of next step, or null if this is the last step
         */
        fun getNextStepIdentifier(taskResult: TaskResult): String?
    }

    /**
     * A navigation skip rule specifies whether a step should be skipped.
     */
    interface SkipStepStrategy {

        /**
         * @param taskResult current task result
         * @return true if step should be skipped
         */
        fun shouldSkip(taskResult: TaskResult): Boolean
    }

    /**
     * A navigation back rule blocks backward navigation for a step.
     */
    interface BackStepStrategy {

        /**
         * Should this step allow backward navigation?
         *
         * @param taskResult current task result
         */
        fun isBackAllowed(taskResult: TaskResult): Boolean
    }

    // make class non-instantiable
    private constructor()
}
