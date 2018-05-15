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

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.Range;
import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.domain.form.DataTypes.InputDataType;
import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.interfaces.ObjectHelper;

import java.util.List;

public class InputFieldBase extends ObjectHelper implements InputField {
    @Nullable
    private final String identifier;
    @Nullable
    private final String prompt;
    @Nullable
    private final String promptDetail;
    @Nullable
    @SerializedName("placeholder")
    private final String placeholderText;
    @SerializedName("optional")
    private final boolean isOptional;
    @SerializedName("dataType")
    @NonNull
    private final InputDataType formDataType;
    @SerializedName("uiHint")
    @Nullable
    @InputUIHint
    private final String formUIHint;
    @Nullable
    private final TextFieldOptions textFieldOptions;
    @Nullable
    private final Range range;
    @Nullable
    private final List<SurveyRule> surveyRules;

    /**
     * Default initializer for gson.
     */
    public InputFieldBase() {
        super();
        this.identifier = null;
        this.prompt = null;
        this.promptDetail = null;
        this.placeholderText = null;
        this.isOptional = false;
        this.formDataType = null;
        this.formUIHint = null;
        this.textFieldOptions = null;
        this.range = null;
        this.surveyRules = null;
    }

    public InputFieldBase(@Nullable final String identifier, @Nullable final String prompt,
            @Nullable final String promptDetail,
            @Nullable final String placeholderText, final boolean isOptional, @NonNull final InputDataType formDataType,
            @Nullable final String formUIHint,
            @Nullable final TextFieldOptions textFieldOptions, @Nullable final Range range,
            @Nullable final List<SurveyRule> surveyRules) {
        super();
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

    @Nullable
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
    @Override
    public InputDataType getFormDataType() {
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

    @Override
    public String toString() {
        return this.toStringHelper().toString();
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.identifier, this.prompt, this.promptDetail, this.placeholderText, this.isOptional,
                        this.formDataType, this.formUIHint, this.textFieldOptions, this.range, this.surveyRules);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("identifier", identifier)
                .add("prompt", prompt)
                .add("promptDetail", promptDetail)
                .add("placeholder", placeholderText)
                .add("optional", isOptional)
                .add("dataType", formDataType)
                .add("uiHint", formUIHint)
                .add("textFieldOptions", textFieldOptions)
                .add("range", range)
                .add("surveyRules", surveyRules);
    }

    @Override
    protected boolean equalsHelper(Object o) {
        InputFieldBase inputField = (InputFieldBase) o;
        return Objects.equal(this.getIdentifier(), inputField.getIdentifier()) &&
                Objects.equal(this.getPrompt(), inputField.getPrompt()) &&
                Objects.equal(this.getPromptDetail(), inputField.getPromptDetail()) &&
                Objects.equal(this.getPlaceholderText(), inputField.getPlaceholderText()) &&
                Objects.equal(this.isOptional(), inputField.isOptional()) &&
                Objects.equal(this.getFormDataType(), inputField.getFormDataType()) &&
                Objects.equal(this.getFormUIHint(), inputField.getFormUIHint()) &&
                Objects.equal(this.getTextFieldOptions(), inputField.getTextFieldOptions()) &&
                Objects.equal(this.getRange(), inputField.getRange()) &&
                Objects.equal(this.getSurveyRules(), inputField.getSurveyRules());
    }
}
