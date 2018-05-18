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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.threeten.bp.Instant;

/**
 * A ResultData stores the state common to all results.
 */
@AutoValue
public abstract class ResultData {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract ResultData build();

        public abstract Builder setIdentifier(final String identifier);

        public abstract Builder setStartTime(final Instant startTime);

        public abstract Builder setEndTime(@Nullable final Instant endTime);
    }

    public static Builder builder() {
        return new AutoValue_ResultData.Builder();
    }

    public abstract Builder toBuilder();

    public static TypeAdapter<ResultData> typeAdapter(Gson gson) {
        return new AutoValue_ResultData.GsonTypeAdapter(gson);
    }

    public static ResultData create(@NonNull final String identifier,
            @NonNull final Instant startTime, @Nullable final Instant endTime) {
        return ResultData.builder()
                .setIdentifier(identifier)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();
    }

    @NonNull
    public abstract String getIdentifier();

    @NonNull
    public abstract Instant getStartTime();

    @Nullable
    public abstract Instant getEndTime();
}
