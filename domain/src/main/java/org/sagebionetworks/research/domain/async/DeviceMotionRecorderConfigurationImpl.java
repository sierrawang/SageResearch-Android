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

package org.sagebionetworks.research.domain.async;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.Set;

@AutoValue
public abstract class DeviceMotionRecorderConfigurationImpl implements DeviceMotionRecorderConfiguration {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract DeviceMotionRecorderConfigurationImpl build();

        public abstract Builder setFrequency(@Nullable Double frequency);

        public abstract Builder setIdentifier(@NonNull String identifier);

        public abstract Builder setRecorderTypes(@NonNull Set<String> recorderTypes);

        public abstract Builder setStartStepIdentifier(@Nullable String startStepIdentifier);

        public abstract Builder setStopStepIdentifier(@Nullable String stopStepIdentifier);
    }

    public static final String TYPE_KEY = RecorderType.MOTION;

    public static Builder builder() {
        return new AutoValue_DeviceMotionRecorderConfigurationImpl.Builder();
    }

    public static TypeAdapter<DeviceMotionRecorderConfigurationImpl> typeAdapter(Gson gson) {
        return new AutoValue_DeviceMotionRecorderConfigurationImpl.GsonTypeAdapter(gson);
    }

    public abstract Builder toBuilder();

    @Override
    @NonNull
    public DeviceMotionRecorderConfigurationImpl copyWithIdentifier(@NonNull String identifier) {
        return this.toBuilder().setIdentifier(identifier).build();
    }

    @Override
    @NonNull
    public DeviceMotionRecorderConfigurationImpl copyWithStartStepIdentifier(@Nullable String startStepIdentifier) {
        return this.toBuilder().setStartStepIdentifier(startStepIdentifier).build();
    }

    @Override
    @NonNull
    public DeviceMotionRecorderConfigurationImpl copyWithStopStepIdentifier(@Nullable String stopStepIdentifier) {
        return this.toBuilder().setStopStepIdentifier(stopStepIdentifier).build();
    }

    @Override
    @NonNull
    @RecorderType
    public String getType() {
        return TYPE_KEY;
    }
}
