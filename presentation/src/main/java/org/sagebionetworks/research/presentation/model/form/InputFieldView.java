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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import org.sagebionetworks.research.domain.form.InputUIHint;
import org.sagebionetworks.research.domain.form.TextField.TextFieldOptions;
import org.sagebionetworks.research.domain.form.data_types.InputDataType;
import org.sagebionetworks.research.domain.survey.SurveyRule;
import org.sagebionetworks.research.presentation.DisplayString;

public interface InputFieldView<E extends Comparable> {
    /**
     * @return data type for this input field. The data type can have an associated ui hint
     */
    @NonNull
    InputDataType getFormDataType();

    /**
     * @return UI hint for how the study would prefer that the input field is displayed to the user
     */
    @Nullable
    @InputUIHint
    String getFormUIHint();

    /**
     * @return identifier that is unique among form items within the step
     */
    @Nullable
    String getIdentifier();

    /**
     * @return text for display in a text field or text area to help users understand how to answer the item's
     * question
     */
    @Nullable
    DisplayString getPlaceholderText();

    /**
     * @return short text offering hint for data to be entered
     */
    @Nullable
    DisplayString getPrompt();

    /**
     * @return text for display giving additional detail about this input field.
     */
    @Nullable
    DisplayString getPromptDetail();

    /**
     * @return The range used by dates and numbers for setting up a picker wheel, slider, or providing text input
     * validation, or null if this is not applicable
     */
    @Nullable
    Range<E> getRange();

    /**
     * @return the list of survey rules that are used by this input field or null if this is not applicable.
     */
    @Nullable
    ImmutableList<? extends SurveyRule> getSurveyRules();

    /**
     * @return The text field options for this InputField or null if there are none.
     */
    @Nullable
    TextFieldOptions getTextFieldOptions();

    /**
     * @return true if this survey option is optional, false otherwise.
     */
    boolean isOptional();
}
