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

package org.sagebionetworks.research.domain.result.implementations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

import org.sagebionetworks.research.domain.Schema;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.result.ResultType;
import org.sagebionetworks.research.domain.result.data.TaskResultData;
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * The concrete implementation of the result for a Task. A wrapper around TaskResultData which adds behavior and can
 * be subclassed
 */
public class TaskResultBase extends ResultBase implements TaskResult {
    @ResultType
    public static final String TYPE_KEY = ResultType.TASK;

    // Subclasses shouldn't hide this field as doing so will result in a gson error.
    private final TaskResultData taskResultData;

    public TaskResultBase(@NonNull String identifier, @NonNull Instant startDate,
            @Nullable Instant endDate, @NonNull UUID taskUUID, @Nullable Schema schema,
            @NonNull List<Result> stepHistory, @NonNull List<Result> asyncResults) {
        super(identifier, startDate, endDate);
        this.taskResultData = TaskResultData.create(taskUUID, schema, stepHistory, asyncResults);
    }

    protected TaskResultBase(@NonNull final Result result, @NonNull final TaskResultData data) {
        super(result.getIdentifier(), result.getStartTime(), result.getEndTime());
        this.taskResultData = data;
    }

    /**
     * Convenience constructor for creating a TaskResultBase with fewer parameters.
     *
     * @param identifier
     *         The identifier of the task result base to create.
     * @param startTime
     *         The task results start time.
     * @param taskUUID
     *         The UUID for the task result.
     */
    public TaskResultBase(@NonNull String identifier, @NonNull Instant startTime, @NonNull UUID taskUUID) {
        this(identifier, startTime, null, taskUUID, null, new ArrayList<Result>(),
                new ArrayList<Result>());
    }

    @NonNull
    @Override
    public TaskResultBase addAsyncResult(final Result result) {
        List<Result> asyncResults = replaceAndAppendResult(this.getAsyncResults(), result);
        TaskResultData newData = TaskResultData
                .create(this.getTaskUUID(), this.getSchemaInfo(), this.getStepHistory(),
                        asyncResults);
        return new TaskResultBase(this, newData);
    }

    @NonNull
    @Override
    public TaskResultBase addStepHistory(final Result result) {
        List<Result> stepHistory = replaceAndAppendResult(this.getStepHistory(), result);
        TaskResultData newData = TaskResultData.create(this.taskResultData, stepHistory);
        return new TaskResultBase(this, newData);
    }

    @Nullable
    @Override
    public AnswerResult getAnswerResult(final Step step) {
        return getAnswerResult(step.getIdentifier());
    }

    @Nullable
    @Override
    public AnswerResult getAnswerResult(final String identifier) {
        Result result = this.getResult(identifier);
        if (result != null && result instanceof AnswerResult) {
            return (AnswerResult) result;
        }

        return null;
    }

    @Override
    public List<Result> getAsyncResults() {
        return this.taskResultData.getAsyncResults();
    }

    @Nullable
    @Override
    public Result getResult(final Step step) {
        return this.getResult(step.getIdentifier());
    }

    @Nullable
    @Override
    public Result getResult(final String identifier) {
        for (Result result : this.getStepHistory()) {
            if (result.getIdentifier().equals(identifier)) {
                return result;
            }
        }

        return null;
    }

    @Override
    public Schema getSchemaInfo() {
        return this.taskResultData.getSchema();
    }

    @Override
    public List<Result> getStepHistory() {
        return this.taskResultData.getStepHistory();
    }

    @Override
    public UUID getTaskUUID() {
        return this.taskResultData.getUUID();
    }

    @NonNull
    @Override
    public TaskResultBase removeStepHistory(final Result result) {
        List<Result> stepHistory = new ArrayList<>(this.getStepHistory());
        Iterator<Result> iterator = stepHistory.iterator();
        boolean foundResult = false;
        while (iterator.hasNext()) {
            Result stepResult = iterator.next();
            if (!foundResult && stepResult.getIdentifier().equals(result.getIdentifier())) {
                foundResult = true;
            }

            if (foundResult) {
                iterator.remove();
            }
        }

        TaskResultData newData = TaskResultData.create(this.taskResultData, stepHistory);
        return new TaskResultBase(this, newData);
    }

    @NonNull
    @ResultType
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        TaskResultBase taskResult = (TaskResultBase) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.taskResultData, taskResult.taskResultData);
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.taskResultData);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("TaskResultData", this.taskResultData);
    }

    /**
     * Returns a copy of the given list with all occurrences of results with the same identifier as the given result
     * removed, and then appends the given result to the end of the given list.
     *
     * @param list
     *         The list of results to remove from and append to.
     * @param result
     *         The result to remove duplicates of and then append.
     * @return A copy of the given list with all duplicates of the given result removed and then the result appended
     * to the end.
     */
    private static List<Result> replaceAndAppendResult(List<Result> list, final Result result) {
        List<Result> returnValue = new ArrayList<>(list);
        Iterator<Result> iterator = returnValue.iterator();
        while (iterator.hasNext()) {
            Result stepResult = iterator.next();
            if (stepResult.getIdentifier().equals(result.getIdentifier())) {
                iterator.remove();
            }
        }

        returnValue.add(result);
        return returnValue;
    }
}
