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

package org.sagebionetworks.research.domain.survey;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.result.interfaces.Result;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A survey rule defines the navigation and cohorts for a survey. Used to allow surveys to have logic behind which
 * questions get asked depending on user data and answers to previous questions.
 */
public interface SurveyRule {

    /**
     * A class to store the result of a call to evaluate cohorts.
     */
    class CohortResult {
        public final ImmutableSet<String> add;

        public final ImmutableSet<String> remove;

        public CohortResult(ImmutableSet<String> add, ImmutableSet<String> remove) {
            this.add = add;
            this.remove = remove;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Operator.SKIPS,
            Operator.EQUALS,
            Operator.NOT_EQUALS,
            Operator.LESS_THAN,
            Operator.GREATER_THAN,
            Operator.LESS_THAN_EQUALS,
            Operator.GREATER_THAN_EQUALS
    })
    @interface Operator {
        String SKIPS = "de";
        String EQUALS = "eq";
        String NOT_EQUALS = "ne";
        String LESS_THAN = "lt";
        String GREATER_THAN = "gt";
        String LESS_THAN_EQUALS = "le";
        String GREATER_THAN_EQUALS = "ge";
    }

    /**
     * For the given result, what are the cohorts to add or remove?
     *
     * @param result
     *         The result to evaluate
     * @return The cohorts to add, and remove
     */
    @Nullable
    CohortResult evaluateCohorts(Result result);

    /**
     * For the given result, what is the next step the survey should go to.
     *
     * @param result
     *         The result to evaluate
     * @return The identifier to skip to or null if this is not applicable.
     */
    @Nullable
    String evaluateRule(Result result);
}
