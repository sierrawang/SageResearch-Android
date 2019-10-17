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

package org.sagebionetworks.research.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.ActiveUIStep;
import org.sagebionetworks.research.domain.step.ui.action.Action;
import org.sagebionetworks.research.domain.step.ui.theme.ColorTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;

import java.util.Map;
import java.util.Set;

@AutoValue
public abstract class InstructionStep implements ActiveUIStep {
    @AutoValue.Builder
    public abstract static class Builder {
        @NonNull
        public abstract InstructionStep build();

        @NonNull
        public abstract Builder setActions(@NonNull Map<String, Action> actions);

        @NonNull
        public abstract Builder setAsyncActions(@NonNull Set<AsyncActionConfiguration> asyncActions);

        @NonNull
        public abstract Builder setBackgroundAudioRequired(boolean isBackgroundAudioRequired);

        @NonNull
        public abstract Builder setColorTheme(@Nullable ColorTheme colorTheme);

        @NonNull
        public abstract Builder setCommands(@NonNull Set<String> commands);

        @NonNull
        public abstract Builder setDetail(@Nullable String detail);

        @NonNull
        public abstract Builder setDuration(@Nullable Double duration);

        @NonNull
        public abstract Builder setFootnote(@Nullable String footnote);

        @NonNull
        public abstract Builder setHiddenActions(@NonNull Set<String> hiddenActions);

        @NonNull
        public abstract Builder setIdentifier(@NonNull String identifier);

        @NonNull
        public abstract Builder setImageTheme(@Nullable ImageTheme imageTheme);

        @NonNull
        public abstract Builder setSpokenInstructions(@NonNull Map<String, String> spokenInstructions);

        @NonNull
        public abstract Builder setText(@Nullable String text);

        @NonNull
        public abstract Builder setTitle(@Nullable String title);
    }

    public static final String TYPE_KEY = StepType.INSTRUCTION;

    @NonNull
    public static Builder builder() {
        return new AutoValue_InstructionStep.Builder();
    }

    public static TypeAdapter<InstructionStep> typeAdapter(Gson gson) {
        return new AutoValue_InstructionStep.GsonTypeAdapter(gson)
                .setDefaultActions(ImmutableMap.of())
                .setDefaultAsyncActions(ImmutableSet.of())
                .setDefaultCommands(ImmutableSet.of())
                .setDefaultHiddenActions(ImmutableSet.of())
                .setDefaultSpokenInstructions(ImmutableMap.of());
    }

    @NonNull
    @Override
    public InstructionStep copyWithIdentifier(@NonNull String identifier) {
        return toBuilder().setIdentifier(identifier).build();
    }

    @NonNull
    @Override
    public final String getType() {
        return StepType.INSTRUCTION;
    }

    @NonNull
    abstract Builder toBuilder();
}
