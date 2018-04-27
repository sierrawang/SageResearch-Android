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

package org.sagebionetworks.research.domain.result;

import android.support.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.threeten.bp.Instant;


public class TaskResult extends ResultBase {
    @NonNull
    private final UUID taskRunUUID;

    @NonNull
    private final ImmutableList<Result> stepHistory;

    @NonNull
    private final ImmutableSet<Result> asyncResults;

    public TaskResult(@NonNull final String identifier, @NonNull UUID taskRunUUID, @NonNull Instant startTime,
        @NonNull Instant endTime, List<Result> stepHistory, Set<Result> asyncResults) {
        super(identifier, ResultType.TASK, startTime, endTime);
        this.taskRunUUID = taskRunUUID;
        this.stepHistory = ImmutableList.copyOf(stepHistory);
        this.asyncResults = ImmutableSet.copyOf(asyncResults);
    }

    @NonNull
    public UUID getTaskRunUUID() {
        return taskRunUUID;
    }

    @NonNull
    public ImmutableList<Result> getStepHistory() {
        return stepHistory;
    }

    @NonNull
    public ImmutableSet<Result> getAsyncResults() {
        return asyncResults;
    }

    public static class Builder {
        private final String identifier;

        private final UUID taskRunUUID;

        private final Map<String, Result> asyncResults;

        private final List<Result> stepHistory;

        private Instant startTime;

        private Instant endTime;

        public Builder(@NonNull String identifier, @NonNull final UUID taskRunUUID) {
            this.identifier = identifier;
            this.taskRunUUID = taskRunUUID;
            stepHistory = new ArrayList<>();
            asyncResults = new HashMap<>();
        }

        public Builder setStartTime(final Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(final Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder addStepResult(Result result) {
            // TODO: fix logic for moving back
            stepHistory.add(result);
            return this;
        }

        public Builder addAsyncResult(Result result) {
            asyncResults.put(result.getIdentifier(), result);
            return this;
        }

        public TaskResult build() {
            return new TaskResult(identifier, taskRunUUID, startTime, endTime, stepHistory,
                new HashSet<>(asyncResults.values()));
        }
    }
}
