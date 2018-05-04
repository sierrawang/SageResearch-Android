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

package org.sagebionetworks.research.domain.form;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.domain.result.Result;

public class ChoiceBase<E> implements Choice<E> {
    @NonNull
    @SerializedName("value")
    private final E answerValue;
    @NonNull
    private final String text;
    @Nullable
    private final String detail;
    @Nullable
    @SerializedName("icon")
    private final String iconName;
    private final boolean isExclusive;

    public ChoiceBase(@NonNull final E answerValue, @NonNull final String text, @Nullable final String detail,
            @Nullable final String iconName, final boolean isExclusive) {
        this.answerValue = answerValue;
        this.text = text;
        this.detail = detail;
        this.iconName = iconName;
        this.isExclusive = isExclusive;
    }

    public ChoiceBase(@NonNull final E answerValue) {
        this.answerValue = answerValue;
        this.text = answerValue.toString();
        this.detail = null;
        this.iconName = null;
        this.isExclusive = false;
    }


    @NonNull
    @Override
    public E getAnswerValue() {
        return this.answerValue;
    }

    @NonNull
    @Override
    public String getText() {
        return this.text;
    }

    @Nullable
    @Override
    public String getDetail() {
        return this.detail;
    }

    @Nullable
    @Override
    public String getIconName() {
        return this.iconName;
    }

    @Override
    public boolean isExclusive() {
        return this.isExclusive;
    }

    @Override
    public boolean isEqualToResult(final Result result) {
        // TODO: rkolmos 05/01/2018 implement this method.
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("answerValue", this.getAnswerValue())
                .add("text", this.getText())
                .add("detail", this.getDetail())
                .add("iconName", this.getIconName())
                .add("isExclusive", this.isExclusive())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        ChoiceBase<?> choice = (ChoiceBase<?>) o;
        return Objects.equal(this.getAnswerValue(), choice.getAnswerValue()) &&
                Objects.equal(this.getText(), choice.getText()) &&
                Objects.equal(this.getDetail(), choice.getDetail()) &&
                Objects.equal(this.isExclusive(), choice.isExclusive()) &&
                Objects.equal(this.getIconName(), choice.getIconName());
    }
}
