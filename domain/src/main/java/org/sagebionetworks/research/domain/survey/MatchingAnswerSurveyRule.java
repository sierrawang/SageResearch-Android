package org.sagebionetworks.research.domain.survey;

import androidx.annotation.Nullable;

/**
 * A survey rule which matches an expected result to an an answer and provides a skip identifier if the match
 * evaluates to true.
 */
public interface MatchingAnswerSurveyRule extends SurveyRule, MatchingAnswer {
    /**
     * Optional cohort to assign if the rule matches.
     */
    @Nullable
    String getCohort();

    /**
     * Optional skip identifier, will default to Exit if getCohort() returns null.
     *
     * @return optional skip identifier
     */
    @Nullable
    String getSkipToIdentifier();

    /**
     * Optional operator, will default to <code>Operator.EQUAL</code>
     *
     * @return survey rule operator
     */
    @Operator
    @Nullable
    String getSurveyRuleOperator();
}
