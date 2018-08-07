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

package org.sagebionetworks.research.domain.step.gson;

import static org.sagebionetworks.research.domain.JsonAssetUtil.readJsonFile;

import static org.sagebionetworks.research.domain.JsonAssetUtil.readJsonFile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonParseException;

import org.junit.Test;
import org.sagebionetworks.research.domain.form.data_types.CollectionInputDataType;
import org.sagebionetworks.research.domain.form.implementations.ChoiceBase;
import org.sagebionetworks.research.domain.form.implementations.ChoiceInputField;
import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.form.interfaces.InputField;
import org.sagebionetworks.research.domain.step.implementations.FormUIStepBase;
import org.sagebionetworks.research.domain.step.interfaces.FormUIStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;

import java.io.IOException;

public class FormUIStepGsonTests extends IndividualStepGsonTest {

    @Test
    public void testIcons() {
        ImmutableList.Builder<Choice<Integer>> choiceBuilder = new ImmutableList.Builder<>();
        choiceBuilder.add(new ChoiceBase<>(1, "delighted", "Nothing could be better!",
                "moodScale1", false));
        choiceBuilder.add(new ChoiceBase<>(2, "good", "Life is good.",
                "moodScale2", false));
        choiceBuilder.add(new ChoiceBase<>(3, "so-so", "Things are okay, I guess.",
                "moodScale3", false));
        choiceBuilder.add(new ChoiceBase<>(4, "sad", "I'm feeling a bit down.",
                "moodScale4", false));
        choiceBuilder.add(new ChoiceBase<>(5, "miserable", "I cry into my pillow every night.",
                "moodScale5", false));
        ImmutableList.Builder<InputField> expectedInputFields = new ImmutableList.Builder<>();
        expectedInputFields.add(new ChoiceInputField<Integer>(null, null, null, null,
                false, new CollectionInputDataType("singleChoice", "integer"), "picker",
                null, null, ImmutableList.of(), choiceBuilder.build(), null));
        FormUIStep expected = new FormUIStepBase("imageList", null, null, "Single Choice with Images",
                "Select a single option", null, null, null, null, expectedInputFields.build());
        testCommon(expected, "FormIcons.json");
    }

    @Test(expected = JsonParseException.class)
    public void testIncorrectChoiceType_StringIntoInteger() throws IOException {
        readJsonFile(stepTestComponent.gson(), "steps/FormStringIntoInteger.json", Step.class);
    }

    @Test
    public void testInteger() {
        ImmutableList.Builder<Choice<Integer>> expectedChoices = new Builder<>();
        expectedChoices.add(new ChoiceBase<>(1, "Alpha", null, null, false));
        expectedChoices.add(new ChoiceBase<>(2, "Beta", null, null, false));
        expectedChoices.add(new ChoiceBase<>(3, "Gamma", null, null, false));
        expectedChoices.add(new ChoiceBase<>(0, "None of the above", null, null, true));
        ImmutableList.Builder<InputField> expectedInputFields = new ImmutableList.Builder<>();
        expectedInputFields.add(new ChoiceInputField<>(null, null, null, null, false,
                new CollectionInputDataType("multipleChoice", "integer"), "list",
                null, null, ImmutableList.of(), expectedChoices.build(), null));
        FormUIStep expected = new FormUIStepBase("selectMultiple", null, null, "Multiple Choice",
                "Select as many as you want", null, null, null, null, expectedInputFields.build());
        testCommon(expected, "FormStepInteger.json");
    }

    @Test
    public void testString() {
        ImmutableList.Builder<Choice<String>> expectedChoices = new ImmutableList.Builder<>();
        expectedChoices.add(new ChoiceBase<>("left", "I can only perform this activity with my LEFT hand.",
                null, null, false));
        expectedChoices.add(new ChoiceBase<>("right", "I can only perform this activity with my RIGHT hand.",
                null, null, false));
        expectedChoices.add(new ChoiceBase<>("both", "I can perform this activity with both hands.",
                null, null, false));
        ImmutableList.Builder<InputField> expectedInputFields = new ImmutableList.Builder<>();
        expectedInputFields.add(new ChoiceInputField<>(null, null, null,
                null, false,
                new CollectionInputDataType("singleChoice", "string"), "list",
                null, null, ImmutableList.of(), expectedChoices.build(), null));
        FormUIStep expected = new FormUIStepBase("handSelection", null, null,
                "Which hands are you capable of doing this task with?",
                null, null, null, null, null, expectedInputFields.build());
        testCommon(expected, "FormStepString.json");
    }

    @Test
    public void testString_Shorthand() {
        ImmutableList.Builder<Choice<String>> expectedChocies = new ImmutableList.Builder<>();
        expectedChocies.add(new ChoiceBase<>("alpha", "alpha", null, null, false));
        expectedChocies.add(new ChoiceBase<>("beta", "beta", null, null, false));
        expectedChocies.add(new ChoiceBase<>("charlie", "charlie", null, null, false));
        expectedChocies.add(new ChoiceBase<>("delta", "delta", null, null, false));
        ImmutableList.Builder<InputField> expectedInputFields = new ImmutableList.Builder<>();
        expectedInputFields.add(new ChoiceInputField<>(null, null, null, null,
                false, new CollectionInputDataType("multipleChoice", null), null,
                null, null, ImmutableList.of(), expectedChocies.build(), null));
        FormUIStep expected = new FormUIStepBase("step3", null, null, "Step 3", null, null, null, null, null,
                expectedInputFields.build());
        testCommon(expected, "FormStepStringShorthand.json");
    }
}
