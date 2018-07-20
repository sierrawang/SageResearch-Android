package org.sagebionetworks.research.domain.survey;

import android.support.annotation.Nullable;

public interface MatchingAnswer<T> {
/**
 * Expected answer for a rule.
 */
@Nullable
    T getMatchingAnswer();
            }
