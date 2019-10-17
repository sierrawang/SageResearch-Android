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

package org.sagebionetworks.research.domain.form.implementations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;
import org.sagebionetworks.research.domain.form.data_types.InputDataType;
import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.form.interfaces.ChoiceOptions;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.survey.SurveyRule;

/**
 * This class is the concrete implementation of an input field that has multiple choices for the user to select.
 *
 * @param <E>
 *         The type of the choices that the user may select.
 */
public class ChoiceInputField<E extends Comparable<E>> extends InputFieldBase<E> implements ChoiceOptions<E> {
    @NonNull
    private final ImmutableList<Choice<E>> choices;

    @Nullable
    private final E defaultAnswer;

    /**
     * Default intializer for gson.
     */
    public ChoiceInputField() {
        super();
        this.choices = null;
        this.defaultAnswer = null;
    }

    public ChoiceInputField(@Nullable final String identifier,
            @Nullable final String prompt,
            @Nullable final String promptDetail,
            @Nullable final String placeholderText, final boolean isOptional,
            @NonNull final InputDataType formDataType,
            @Nullable final String formUIHint,
            @Nullable final TextFieldOptions textFieldOptions,
            @Nullable final Range range,
            @NonNull final ImmutableList<SurveyRule> surveyRules,
            @NonNull final ImmutableList<Choice<E>> choices,
            @Nullable final E defaultAnswer) {
        super(identifier, prompt, promptDetail, placeholderText, isOptional, formDataType, formUIHint,
                textFieldOptions, range, surveyRules);
        this.choices = choices;
        this.defaultAnswer = defaultAnswer;
    }

    @NonNull
    @Override
    public ImmutableList<Choice<E>> getChoices() {
        return this.choices;
    }

    /**
     * @return The default answer this ChoiceInputField.
     */
    @Nullable
    @Override
    public E getDefaultAnswer() {
        return this.defaultAnswer;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        ChoiceInputField<?> inputField = (ChoiceInputField<?>) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.getChoices(), inputField.getChoices()) &&
                Objects.equal(this.getDefaultAnswer(), inputField.getDefaultAnswer());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.choices, this.defaultAnswer);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("choices", choices)
                .add("defaultAnswer", defaultAnswer);
    }
}
