package org.sagebionetworks.research.domain.survey;

import android.support.annotation.Nullable;

public interface DecimalMatchingAnswer extends MatchingAnswer<Double> {
    @Nullable
    Double getAccuracy(); //TODO: use BigDecimal?
}
