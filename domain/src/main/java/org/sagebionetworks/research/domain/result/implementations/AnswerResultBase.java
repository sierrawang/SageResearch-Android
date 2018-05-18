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

package org.sagebionetworks.research.domain.result.implementations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.result.AnswerResultType;
import org.sagebionetworks.research.domain.result.ResultType;
import org.sagebionetworks.research.domain.result.data.AnswerResultData;
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult;
import org.threeten.bp.Instant;

/**
 * The concrete implementation of a Result which stores an answer. A wrapper around,
 * AnswerResultData which adds behavior and can be subclassed.
 * @param <T> The type of the answer that is stored.
 */
public class AnswerResultBase<T> extends ResultBase implements AnswerResult<T> {
    @ResultType
    public static final String TYPE_KEY = ResultType.ANSWER;

    // Subclasses should not hide this field, as doing so will result in a gson error.
    protected final AnswerResultData<T> answerResultData;

    public AnswerResultBase(@NonNull final String identifier, @NonNull final Instant startTime,
            @NonNull final Instant endTime, @Nullable final T answer,
            @AnswerResultType String answerResultType) {
        super(identifier, startTime, endTime);
        this.answerResultData = AnswerResultData.create(answer, answerResultType);
    }

    @NonNull
    @ResultType
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Nullable
    @Override
    public T getAnswer() {
        return this.answerResultData.getAnswer();
    }

    @NonNull
    @AnswerResultType
    @Override
    public String getAnswerResultType() {
        return this.answerResultData.getAnswerResultType();
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.answerResultData);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("TaskResultData", this.answerResultData);
    }

    @Override
    protected boolean equalsHelper(Object o) {
        AnswerResultBase answerResult = (AnswerResultBase) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.answerResultData, answerResult.answerResultData);
    }
}
