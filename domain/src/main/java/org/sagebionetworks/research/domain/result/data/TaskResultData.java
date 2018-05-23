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

package org.sagebionetworks.research.domain.result.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.sagebionetworks.research.domain.Schema;
import org.sagebionetworks.research.domain.result.interfaces.Result;

import java.util.List;
import java.util.UUID;

/**
 * A TaskResultData stores the state unique to a task result.
 */
@AutoValue
public abstract class TaskResultData {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract TaskResultData build();

        public abstract Builder setUUID(UUID taskUUID);

        public abstract Builder setSchema(Schema schema);

        public abstract Builder setStepHistory(List<Result> stepHistory);

        public abstract Builder setAsyncResults(List<Result> asyncResults);
    }

    public static TaskResultData create(@NonNull final UUID taskUUID, @Nullable final Schema schema,
            @NonNull final List<Result> stepHistory, @NonNull final List<Result> asyncResults) {
        return TaskResultData.builder()
                .setUUID(taskUUID)
                .setSchema(schema)
                .setStepHistory(stepHistory)
                .setAsyncResults(asyncResults)
                .build();
    }

    public static TaskResultData create(@NonNull final TaskResultData data, @NonNull final List<Result> stepHistory) {
        return TaskResultData.create(data.getUUID(), data.getSchema(), stepHistory, data.getAsyncResults());
    }

    public static TypeAdapter<TaskResultData> typeAdapter(Gson gson) {
        return new AutoValue_TaskResultData.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_TaskResultData.Builder();
    }

    public abstract Builder toBuilder();

    public abstract UUID getUUID();

    public abstract Schema getSchema();

    public abstract List<Result> getStepHistory();

    public abstract List<Result> getAsyncResults();
}
