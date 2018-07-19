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

package org.sagebionetworks.research.presentation.model.form;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;

import java.io.Serializable;

import dagger.Provides;

@AutoValue
public abstract class ChoiceView<E> {
    @AutoValue.Builder
    public abstract static class Builder<E> {
        public abstract ChoiceView<E> build();

        public abstract Builder<E> setAnswerValue(@NonNull final E answerValue);

        public abstract Builder<E> setDetail(@Nullable final DisplayString detail);

        public abstract Builder<E> setExclusive(final boolean isExclusive);

        public abstract Builder<E> setIconResId(final int resId);

        public abstract Builder<E> setText(@Nullable final DisplayString text);
    }

    public static <T> ChoiceView<T> fromChoice(@Nullable Choice<T> choice, DrawableMapper mapper) {
        if (choice == null) {
            return null;
        }

        DisplayString detail = DisplayString.create(0, choice.getDetail());
        DisplayString text = DisplayString.create(0, choice.getText());
        String iconName = choice.getIconName();
        int iconRes = 0;
        if (iconName != null) {
            iconRes = mapper.getDrawableFromName(iconName);
        }

        return ChoiceView.<T>builder()
                .setAnswerValue(choice.getAnswerValue())
                .setText(text)
                .setDetail(detail)
                .setExclusive(choice.isExclusive())
                .setIconResId(iconRes)
                .build();

    }

    public static <T> Builder<T> builder() {
        return new AutoValue_ChoiceView.Builder<T>();
    }

    @NonNull
    public abstract E getAnswerValue();

    @Nullable
    public abstract DisplayString getDetail();

    public abstract int getIconResId();

    @Nullable
    public abstract DisplayString getText();

    public abstract boolean isExclusive();

    public abstract Builder<E> toBuilder();
}
