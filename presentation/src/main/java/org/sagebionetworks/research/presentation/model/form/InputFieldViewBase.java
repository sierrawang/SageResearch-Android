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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Range;

import org.sagebionetworks.research.domain.form.DataTypes.InputDataType;
import org.sagebionetworks.research.domain.form.InputUIHint;
import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;
import org.sagebionetworks.research.domain.form.interfaces.InputField;
import org.sagebionetworks.research.domain.form.interfaces.SurveyRule;
import org.sagebionetworks.research.presentation.DisplayString;

import java.util.ArrayList;
import java.util.List;

public class InputFieldViewBase implements InputFieldView, Parcelable {
    @Nullable
    private final String identifier;
    @Nullable
    private final DisplayString prompt;
    @Nullable
    private final DisplayString promptDetail;
    @Nullable
    private final DisplayString placeholderText;
    @Nullable
    private final boolean isOptional;
    @NonNull
    private final InputDataType formDataType;
    @Nullable
    @InputUIHint
    private final String uiHint;
    @Nullable
    private final TextFieldOptions textFieldOptions;
    @Nullable
    private final Range range;
    @Nullable
    private final List<SurveyRule> surveyRules;

    public InputFieldViewBase(@Nullable final String identifier, @Nullable final DisplayString prompt,
            @Nullable final DisplayString promptDetail, @Nullable final DisplayString placeholderText,
            final boolean isOptional, @NonNull final InputDataType formDataType, @Nullable final String uiHint,
            @Nullable final TextFieldOptions textFieldOptions, @Nullable final Range range,
            @Nullable final List<SurveyRule> surveyRules) {
        this.identifier = identifier;
        this.prompt = prompt;
        this.promptDetail = promptDetail;
        this.placeholderText = placeholderText;
        this.isOptional = isOptional;
        this.formDataType = formDataType;
        this.uiHint = uiHint;
        this.textFieldOptions = textFieldOptions;
        this.range = range;
        this.surveyRules = surveyRules;
    }

    public static InputFieldViewBase fromInputField(InputField inputField) {
        String identifier = inputField.getIdentifier();
        boolean isOptional = inputField.isOptional();
        InputDataType formDataType = inputField.getFormDataType();
        @InputUIHint String uiHint = inputField.getFormUIHint();
        TextFieldOptions textFieldOptions = inputField.getTextFieldOptions();
        Range range = inputField.getRange();
        List<SurveyRule> surveyRules = inputField.getSurveyRules();
        // TODO rkolmos 05/30/2018 do the correct thing with these strings
        DisplayString prompt = new DisplayString(0, inputField.getPrompt());
        DisplayString promptDetail = new DisplayString(0, inputField.getPromptDetail());
        DisplayString placeholderText = new DisplayString(0, inputField.getPlaceholderText());

        return new InputFieldViewBase(identifier, prompt, promptDetail, placeholderText, isOptional, formDataType,
                uiHint, textFieldOptions, range, surveyRules);
    }

    @Override
    @Nullable
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @Nullable
    public DisplayString getPrompt() {
        return prompt;
    }

    @Override
    @Nullable
    public DisplayString getPromptDetail() {
        return promptDetail;
    }

    @Override
    @Nullable
    public DisplayString getPlaceholderText() {
        return placeholderText;
    }

    @Override
    @Nullable
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    @NonNull
    public InputDataType getFormDataType() {
        return formDataType;
    }

    @Override
    @Nullable
    public String getFormUIHint() {
        return uiHint;
    }

    @Override
    @Nullable
    public TextFieldOptions getTextFieldOptions() {
        return textFieldOptions;
    }

    @Override
    @Nullable
    public Range getRange() {
        return range;
    }

    @Override
    @Nullable
    public List<SurveyRule> getSurveyRules() {
        return surveyRules;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeParcelable(this.prompt, flags);
        dest.writeParcelable(this.promptDetail, flags);
        dest.writeParcelable(this.placeholderText, flags);
        dest.writeByte(this.isOptional ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.formDataType, flags);
        dest.writeString(this.uiHint);
        dest.writeParcelable(this.textFieldOptions, flags);
        dest.writeSerializable(this.range);
        dest.writeList(this.surveyRules);
    }

    protected InputFieldViewBase(Parcel in) {
        this.identifier = in.readString();
        this.prompt = in.readParcelable(DisplayString.class.getClassLoader());
        this.promptDetail = in.readParcelable(DisplayString.class.getClassLoader());
        this.placeholderText = in.readParcelable(DisplayString.class.getClassLoader());
        this.isOptional = in.readByte() != 0;
        this.formDataType = in.readParcelable(InputDataType.class.getClassLoader());
        this.uiHint = in.readString();
        this.textFieldOptions = in.readParcelable(TextFieldOptions.class.getClassLoader());
        this.range = (Range) in.readSerializable();
        this.surveyRules = new ArrayList<SurveyRule>();
        in.readList(this.surveyRules, SurveyRule.class.getClassLoader());
    }

    public static final Creator<InputFieldViewBase> CREATOR = new Creator<InputFieldViewBase>() {
        @Override
        public InputFieldViewBase createFromParcel(Parcel source) {
            return new InputFieldViewBase(source);
        }

        @Override
        public InputFieldViewBase[] newArray(int size) {
            return new InputFieldViewBase[size];
        }
    };
}
