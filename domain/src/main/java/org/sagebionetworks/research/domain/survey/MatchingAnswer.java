package org.sagebionetworks.research.domain.survey;

import androidx.annotation.Nullable;

public interface MatchingAnswer<T> {
/**
 * Expected answer for a rule.
 */
@Nullable
    T getMatchingAnswer();
            }
