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

package org.sagebionetworks.research.domain.task.navigation

import org.sagebionetworks.research.domain.result.interfaces.TaskResult
import org.sagebionetworks.research.domain.step.interfaces.Step

/**
 * RSDStepNavigator` is the model object used by the `RSDTaskController` to determine the order of
 * presentation of the steps in a task.
 */
interface StepNavigator {

    /**
     * Returns the step associated with a given identifier.
     *
     * @param identifier step identifier
     * @return step for identifier, or null if not found
     */
    fun getStep(identifier: String): Step?

    /**
     * Given the current task result, is there a step before the current step?
     *
     * This method is checked when first displaying a step to determine if the UI should display this as the first
     * step. By default, the UI defined in ResearchStack2UI will hide the "BackStepStrategy" button if there is no step before
     * the given step.
     *
     * @param step current step, or null if retrieving first step for TaskInfo
     * @param taskResult current step result
     * @return next step, or null if there is no next step
     */
    fun getNextStep(step: Step?, taskResult: TaskResult): Step?

    /**
     * Get the step to go to before the given step.
     *
     * @param step current step
     * @param taskResult current step result
     * @return previous step, or null if there is no previous step
     */
    fun getPreviousStep(step: Step, taskResult: TaskResult): Step?

    /**
     * Return the progress through the task for a given step with the current result.
     *
     * @param step current step
     * @param taskResult current step result
     * @return progress within the task
     */
    fun getProgress(step: Step, taskResult: TaskResult): TaskProgress?

    /**
     * Return the list of steps that are part of this task. Not necessarily in order.
     * @retrun the list of steps that are part of this task not necessarily in order.
     */
    fun getSteps() : List<Step>
}
