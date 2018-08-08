package org.sagebionetworks.research.domain.step.implementations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.auto.value.AutoValue;
import com.google.auto.value.AutoValue.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.CountdownStep;
import org.sagebionetworks.research.domain.step.ui.action.Action;
import org.sagebionetworks.research.domain.step.ui.theme.ColorTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;
import org.threeten.bp.Instant;

import java.util.Map;
import java.util.Set;

@AutoValue
public abstract class CountdownStepImpl implements CountdownStep {
    public static final String TYPE_KEY = StepType.COUNTDOWN;

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract CountdownStepImpl build();

        @NonNull
        public abstract Builder setActions(@NonNull Map<String, Action> actions);

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

    public static TypeAdapter<CountdownStepImpl> typeAdapter(Gson gson) {
        return new AutoValue_CountdownStepImpl.GsonTypeAdapter(gson)
                .setDefaultActions(ImmutableMap.of())
                .setDefaultCommands(ImmutableSet.of())
                .setDefaultHiddenActions(ImmutableSet.of())
                .setDefaultSpokenInstructions(ImmutableMap.of());
    }

    public static Builder builder() {
        return new AutoValue_CountdownStepImpl.Builder();
    }

    public abstract Builder toBuilder();

    @Override
    @NonNull
    public CountdownStepImpl copyWithIdentifier(@NonNull String identifier) {
        return this.toBuilder().setIdentifier(identifier).build();
    }

    @Override
    public String getType() {
        return TYPE_KEY;
    }
}
