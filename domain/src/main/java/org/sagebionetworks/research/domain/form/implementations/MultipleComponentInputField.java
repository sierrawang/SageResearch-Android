package org.sagebionetworks.research.domain.form.implementations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;


import org.sagebionetworks.research.domain.form.InputUIHint;
import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;
import org.sagebionetworks.research.domain.form.data_types.InputDataType;
import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.form.interfaces.InputField;
import org.sagebionetworks.research.domain.form.interfaces.MultipleComponentOptions;
import org.sagebionetworks.research.domain.survey.SurveyRule;

@AutoValue
public abstract class MultipleComponentInputField<E extends Comparable<E>>
        implements InputField<E>, MultipleComponentOptions<E> {
    @AutoValue.Builder
    public static abstract class Builder<E extends Comparable<E>> {
        abstract MultipleComponentInputField<E> build();

        abstract Builder<E> setChoices(@NonNull ImmutableList<ImmutableList<Choice<E>>> choices);

        abstract Builder<E> setDefaultAnswer(@Nullable final E defaultAnswer);

        abstract Builder<E> setFormDataType(@NonNull InputDataType inputDataType);

        abstract Builder<E> setFormUIHint(@Nullable @InputUIHint String formUIHint);

        abstract Builder<E> setIdentifier(@NonNull String identifier);

        abstract Builder<E> setOptional(boolean optional);

        abstract Builder<E> setPlaceholderText(@Nullable String placeholderText);

        abstract Builder<E> setPrompt(@Nullable String prompt);

        abstract Builder<E> setPromptDetail(@Nullable String setPromptDetail);

        abstract Builder<E> setRange(@Nullable Range<E> range);

        abstract Builder<E> setSurveyRules(@NonNull ImmutableList<? extends SurveyRule> surveyRules);

        abstract Builder<E> setTextFieldOptions(@Nullable TextFieldOptions textFieldOptions);
    }

    @Nullable
    @Override
    public abstract E getDefaultAnswer();

//    public static TypeAdapter<MultipleComponentInputField> typeAdapter(Gson gson) {
//        return new AutoValue_MultipleComponentInputField.GsonTypeAdapter(gson);
//    }

    abstract Builder<E> toBuilder();
}
