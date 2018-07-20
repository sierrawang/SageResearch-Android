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

package org.sagebionetworks.research.domain.task.navigation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.sagebionetworks.research.domain.async.AsyncAction;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.ui.action.Action;
import org.sagebionetworks.research.domain.task.Task;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoValue
public abstract class TaskBase implements Task {

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract TaskBase build();

        public abstract Builder setActions(@NonNull Map<String, Action> actions);

        public abstract Builder setAsyncActions(@NonNull List<AsyncAction> asyncActions);

        public abstract Builder setHiddenActions(@NonNull                Set<String> hiddenActions);

        public abstract Builder setIdentifier(@NonNull String identifier);

        public abstract Builder setProgressMarkers(@NonNull List<String> progressMarkers);

        public abstract Builder setSteps(@NonNull List<Step> steps);
    }

    public static Builder builder() {
        return new AutoValue_TaskBase.Builder()
                .setAsyncActions(Collections.<AsyncAction>emptyList());
    }

    public static TypeAdapter<TaskBase> typeAdapter(Gson gson) {
        return new AutoValue_TaskBase.GsonTypeAdapter(gson)
                .setDefaultSteps(ImmutableList.<Step>of())
                .setDefaultProgressMarkers(ImmutableList.<String>of())
                .setDefaultAsyncActions(ImmutableList.<AsyncAction>of())
                .setDefaultActions(ImmutableMap.of())
                .setDefaultHiddenActions(ImmutableSet.of());
    }

    @NonNull
    @Override
    public Task copyWithSteps(final List<Step> steps) {
        return this.builder()
                .setIdentifier(this.getIdentifier())
                .setAsyncActions(this.getAsyncActions())
                .setSteps(steps)
                .setProgressMarkers(this.getProgressMarkers())
                .build();
    }
}
