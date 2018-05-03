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

package org.sagebionetworks.research.domain.form;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Range;

import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;

import java.util.List;

public class InputFieldBase implements InputField {
    @NonNull
    @InputDataType
    private final String formDataType;

    @Nullable
    @InputUIHint
    private final String formUIHint;

    @NonNull
    private final String identifier;

    private final boolean isOptional;

    @Nullable
    private final String placeholderText;

    @Nullable
    private final String prompt;

    @Nullable
    private final String promptDetail;

    @Nullable
    private final Range range;

    @Nullable
    private final List<SurveyRule> surveyRules;

    @Nullable
    private final TextFieldOptions textFieldOptions;

    public InputFieldBase(@NonNull final String identifier, @Nullable final String prompt,
            @Nullable final String promptDetail,
            @Nullable final String placeholderText, final boolean isOptional, @NonNull final String formDataType,
            @Nullable final String formUIHint,
            @Nullable final TextFieldOptions textFieldOptions, @Nullable final Range range,
            @Nullable final List<SurveyRule> surveyRules) {
        this.identifier = identifier;
        this.prompt = prompt;
        this.promptDetail = promptDetail;
        this.placeholderText = placeholderText;
        this.isOptional = isOptional;
        this.formDataType = formDataType;
        this.formUIHint = formUIHint;
        this.textFieldOptions = textFieldOptions;
        this.range = range;
        this.surveyRules = surveyRules;
    }

    @NonNull
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Nullable
    @Override
    public String getPrompt() {
        return this.prompt;
    }

    @Nullable
    @Override
    public String getPromptDetail() {
        return this.promptDetail;
    }

    @Nullable
    @Override
    public String getPlaceholderText() {
        return this.placeholderText;
    }

    @Override
    public boolean isOptional() {
        return this.isOptional;
    }

    @NonNull
    @InputDataType
    @Override
    public String getFormDataType() {
        return this.formDataType;
    }

    @Nullable
    @InputUIHint
    @Override
    public String getFormUIHint() {
        return this.formUIHint;
    }

    @Nullable
    @Override
    public TextFieldOptions getTextFieldOptions() {
        return this.textFieldOptions;
    }

    @Nullable
    @Override
    public Range getRange() {
        return this.range;
    }

    @Nullable
    @Override
    public List<SurveyRule> getSurveyRules() {
        return this.surveyRules;
    }
}
