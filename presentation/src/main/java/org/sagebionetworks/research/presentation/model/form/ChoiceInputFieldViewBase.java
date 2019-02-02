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

package org.sagebionetworks.research.presentation.model.form;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;
import org.sagebionetworks.research.domain.form.data_types.InputDataType;
import org.sagebionetworks.research.domain.survey.SurveyRule;
import org.sagebionetworks.research.presentation.DisplayString;

import java.util.List;

public class ChoiceInputFieldViewBase<E extends Comparable<E>> extends InputFieldViewBase<E> {
    private final ImmutableList<ChoiceView<E>> choices;

    private final E defaultAnswer;

    public ChoiceInputFieldViewBase(final String identifier,
            final DisplayString prompt, final DisplayString promptDetail,
            final DisplayString placeholderText, final boolean isOptional,
            @NonNull final InputDataType formDataType, final String uiHint,
            final TextFieldOptions textFieldOptions, final Range range,
            final ImmutableList<? extends SurveyRule> surveyRules,
            final ImmutableList<ChoiceView<E>> choices, final E defaultAnswer) {
        super(identifier, prompt, promptDetail, placeholderText, isOptional, formDataType, uiHint, textFieldOptions,
                range, surveyRules);
        this.choices = choices;
        this.defaultAnswer = defaultAnswer;
    }

    public ChoiceInputFieldViewBase(final Parcel in,
            final ImmutableList<ChoiceView<E>> choices, final E defaultAnswer) {
        super(in);
        this.choices = choices;
        this.defaultAnswer = defaultAnswer;
    }

    public List<ChoiceView<E>> getChoices() {
        return choices;
    }

    public E getDefaultAnswer() {
        return defaultAnswer;
    }
}
