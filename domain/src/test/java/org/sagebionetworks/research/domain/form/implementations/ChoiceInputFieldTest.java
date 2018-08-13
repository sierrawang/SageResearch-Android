package org.sagebionetworks.research.domain.form.implementations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.google.common.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.research.domain.JsonAssetUtil;
import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.form.interfaces.InputField;

import java.util.List;

public class ChoiceInputFieldTest {
    protected InputFieldTestComponent inputFieldTestComponent;

    @Before
    public void setup() {
        inputFieldTestComponent = DaggerInputFieldTestComponent.builder().build();
    }

    @Test
    public void test() {
        List<InputField> inputFields;
        TypeToken<List<InputField>> tt = new TypeToken<List<InputField>>() {
        };
        inputFields = JsonAssetUtil.readJsonFile(inputFieldTestComponent.gson(),
                "input_fields/InputFields.json", tt);

        InputField inputField1 = inputFields.get(0);
        assertTrue(inputField1 instanceof ChoiceInputField);

        ChoiceInputField<String> choiceInputField1 = (ChoiceInputField<String>) inputField1;

        assertChoice(choiceInputField1.getChoices().get(0),
                "left", "I can only perform this activity with my LEFT hand.");
        assertChoice(choiceInputField1.getChoices().get(1),
                "right", "I can only perform this activity with my RIGHT hand.");
        assertChoice(choiceInputField1.getChoices().get(2),
                "both", "I can perform this activity with both hands.");


        InputField inputField2 = inputFields.get(1);
        assertTrue(inputField2 instanceof ChoiceInputField);
        ChoiceInputField<Integer> choiceInputField2 = (ChoiceInputField<Integer>) inputField2;

        assertChoice(choiceInputField2.getChoices().get(0), 1, "Alpha");
        assertChoice(choiceInputField2.getChoices().get(1), 2, "Beta");
        assertChoice(choiceInputField2.getChoices().get(2), 3, "Gamma");
        assertChoice(choiceInputField2.getChoices().get(3), 0, "None of the above");
        assertTrue(choiceInputField2.getChoices().get(3).isExclusive());
}

    private <T extends Comparable<T>> void assertChoice(Choice<T> choice, T answerValue, String text) {
        assertEquals(answerValue, choice.getAnswerValue());
        assertEquals(text, choice.getText());
    }
}