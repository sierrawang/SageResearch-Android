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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.sagebionetworks.research.domain.result.interfaces.ErrorResult.ErrorResultThrowable;

/**
 * An ErrorResultData stores the state unique to an ErrorResult
 */
@AutoValue
public abstract class ErrorResultData {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract ErrorResultData build();

        public abstract Builder setErrorDescription(@NonNull final String errorDescription);

        public abstract Builder setThrowable(@Nullable final ErrorResultThrowable throwable);
    }

    public static Builder builder() {
        return new AutoValue_ErrorResultData.Builder();
    }

    public static ErrorResultData create(final String errorDescription, final ErrorResultThrowable throwable) {
        return ErrorResultData.builder()
                .setErrorDescription(errorDescription)
                .setThrowable(throwable)
                .build();
    }

    public static TypeAdapter<ErrorResultData> typeAdapter(Gson gson) {
        return new AutoValue_ErrorResultData.GsonTypeAdapter(gson);
    }

    @NonNull
    public abstract String getErrorDescription();

    @Nullable
    public abstract ErrorResultThrowable getThrowable();

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getErrorDescription(), this.getThrowable());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ErrorResultData)) {
            return false;
        }

        ErrorResultData errorResult = (ErrorResultData) o;
        // Throwable doesn't implement an equals beyond == so comparing based on this would produce
        // undesired results.
        return Objects.equal(this.getErrorDescription(), errorResult.getErrorDescription())
                && Objects.equal(this.getThrowable(), errorResult.getThrowable());
    }

    public abstract Builder toBuilder();
}
