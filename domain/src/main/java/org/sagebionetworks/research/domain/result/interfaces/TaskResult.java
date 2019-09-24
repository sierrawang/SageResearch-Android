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

package org.sagebionetworks.research.domain.result.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sagebionetworks.research.domain.Schema;
import org.sagebionetworks.research.domain.step.interfaces.Step;

import java.util.List;
import java.util.UUID;

import javax.annotation.RegEx;

/**
 * A TaskResult is the result from an entire task rather than a single step.
 */
public interface TaskResult extends Result {
    /**
     * Returns a new TaskResult with the given result removed from and then appended to the end of the async results.
     *
     * @param result
     *         The result to append to the async results.
     * @return a new task result with the given result appended to the async results.
     */
    TaskResult addAsyncResult(Result result);

    /**
     * Returns a new TaskResult with the old result removed from the step history and appended to the end.
     *
     * @param result
     *         The result to append to the step history.
     * @return a new TaskResult with the given result appended to the step history.
     */
    @NonNull
    TaskResult addStepHistory(Result result);

    /**
     * Convenience method for getting the result corresponding to the given step as an AnswerResult. If there is no
     * corresponding result or the result isn't an AnswerResult returns null.
     *
     * @param step
     *         The step to find the AnswerResult for.
     * @return The AnswerResult corresponding to the given step in this.
     */
    @Nullable
    AnswerResult getAnswerResult(Step step);

    /**
     * Convenience method for getting the result corresponding to the given identifier as an AnswerResult. If there is
     * no corresponding result or the result isn't an AnswerResult returns null.
     *
     * @param identifier
     *         The identifier to find the AnswerResult for.
     * @return The AnswerResult corresponding to the given identifier in this.
     */
    @Nullable
    AnswerResult getAnswerResult(String identifier);

    /**
     * @return The list of async results for this run of the task.
     */
    List<Result> getAsyncResults();

    /**
     * Finds and returns the result corresponding to the given step in this. If there is no result, corresponding to
     * the given step returns null.
     *
     * @param step
     *         The step to find the result for.
     * @return The result corresponding to the given step in this.
     */
    @Nullable
    Result getResult(Step step);

    /**
     * Finds and returns the result with the given identifier in this. If there is no result, corresponding to the
     * given identifier returns null.
     *
     * @param identifier
     *         The identifier to find the result for.
     * @return The result corresponding to the given identifier in this.
     */
    @Nullable
    Result getResult(String identifier);

    /**
     * @return The Schema info for the task represented by this result.
     */
    Schema getSchemaInfo();

    /**
     * @return The list of step results that have executed up to this point in this run of the task.
     */
    List<Result> getStepHistory();

    /**
     * @return The UUID for the task represented by this result.
     */
    UUID getTaskUUID();

    /**
     * Returns a new TaskResult with the given result and all results after it removed from the step history.
     *
     * @param result
     *         The result to begin removing results at.
     * @return a new TaskResult with the given result and all results after it removed from the step histroy.
     */
    @Nullable
    TaskResult removeStepHistory(Result result);

    /**
     * Returns a new TaskResult with the given result and all results after it removed from the async result list.
     *
     * @param result
     *         The result to begin removing results at.
     * @return a new TaskResult with the given result and all results after it removed from he async result list.
     */
    @Nullable
    TaskResult removeAsyncResult(Result result);

    /**
     * Returns a list of Results from this task result whose identifier match the given regex. If
     * no results match the empty list will be returned.
     * @param regex The regex to match in the results identifiers.
     * @return A list of Results whose identifiers match the given regex. If no results match the
     * empty list will be returned.
     */
    @NonNull
    List<Result> getResultsMatchingRegex(@RegEx String regex);
}
