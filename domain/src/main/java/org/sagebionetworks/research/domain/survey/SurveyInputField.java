package org.sagebionetworks.research.domain.survey;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.sagebionetworks.research.domain.form.interfaces.InputField;

public interface SurveyInputField extends InputField {
    @Override
    @NonNull
    ImmutableList<MatchingAnswerSurveyRule> getSurveyRules();
}
