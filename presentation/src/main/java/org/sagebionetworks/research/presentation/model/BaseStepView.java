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

package org.sagebionetworks.research.presentation.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.step.Step;

import java.util.Collections;
import java.util.Set;

/**
 * Map a {@link Step} to a {@link BaseStepView} when data is moving from the Domain layer to this layer.
 */
@AutoValue
public abstract class BaseStepView implements StepView, Parcelable {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract BaseStepView build();

        public abstract Builder setIdentifier(String identifier);

        public abstract Builder setNavDirection(@NavDirection int navDirection);

        public abstract Builder setStepActionViews(Set<StepActionView> stepActionViews);
    }

    public static Builder builder() {
        return new AutoValue_BaseStepView.Builder()
                .setNavDirection(NavDirection.SHIFT_LEFT)
                .setStepActionViews(Collections.emptySet());
    }

    public abstract String getIdentifier();

    public abstract int getNavDirection();

    public abstract ImmutableSet<StepActionView> getStepActionViews();

    public abstract Builder toBuilder();
}
