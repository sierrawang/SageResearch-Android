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

package org.sagebionetworks.research.domain.result.data;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.sagebionetworks.research.domain.result.AnswerResultType;

/**
 * An AnswerResultData stores the state unique to an answer result.
 *
 * @param <T>
 *         The type of the answer that the result stores.
 */
@AutoValue
public abstract class AnswerResultData<T> {
    @AutoValue.Builder
    public abstract static class Builder<T> {
        public abstract AnswerResultData<T> build();

        public abstract Builder<T> setAnswer(@Nullable final T answer);

        public abstract Builder<T> setAnswerResultType(@AnswerResultType final String answerResultType);
    }

    public static <T> Builder<T> builder(@Nullable T answer) {
        return new AutoValue_AnswerResultData.Builder<T>();
    }

    public static <T> AnswerResultData<T> create(@Nullable final T answer, @AnswerResultType final String answerResultType) {
        return AnswerResultData.builder(answer)
                .setAnswer(answer)
                .setAnswerResultType(answerResultType)
                .build();
    }

    public static <T> TypeAdapter<AnswerResultData<T>> typeAdapter(Gson gson,
            TypeToken<? extends AnswerResultData<T>> token) {
        return new AutoValue_AnswerResultData.GsonTypeAdapter(gson, token);
    }

    @Nullable
    public abstract T getAnswer();

    public abstract String getAnswerResultType();

    public abstract Builder<T> toBuilder();
}
