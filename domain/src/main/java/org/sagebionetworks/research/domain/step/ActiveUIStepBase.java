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

package org.sagebionetworks.research.domain.step;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

import org.sagebionetworks.research.domain.step.ui.ActiveUIStep;
import org.threeten.bp.Duration;


public class ActiveUIStepBase extends UIStepBase implements ActiveUIStep {
    public static final String TYPE_KEY = "active";

    @Nullable
    private final Duration duration;
    private final boolean backgroundAudioRequired;

    public ActiveUIStepBase(@NonNull  final String identifier,
            @Nullable final String title, @Nullable final String text,
            @Nullable final String detail, @Nullable final String footnote,
            @Nullable final Duration duration, final boolean backgroundAudioRequired) {
        super(identifier, title, text, detail, footnote);
        this.duration = duration;
        this.backgroundAudioRequired = backgroundAudioRequired;
    }
    @Nullable
    @Override
    public Duration getDuration() {
        return this.duration;
    }

    @NonNull
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    public boolean isBackgroundAudioRequired() {
        return this.backgroundAudioRequired;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 3 * Objects.hashCode(this.duration, this.backgroundAudioRequired);
    }

    /**
     * Override equalsHelepr to also check the equality of Duration and isBackgroundAudioRequired.
     * @param o The object to check for equality with this.
     * @return True if this is equal to other, false otherwise.
     */
    @Override
    protected boolean equalsHelper(Object o) {
        ActiveUIStepBase activeStep = (ActiveUIStepBase) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.getDuration(), activeStep.getDuration()) &&
                this.isBackgroundAudioRequired() == activeStep.isBackgroundAudioRequired();
    }

    /**
     * Override toStringHelper to add the duration and isBackgroundAudioRequired fields to the
     * ToStringHelper.
     * @return The ToStringHelper that can create a String representation of this.
     */
    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("duration", this.getDuration())
                .add("isBackgroundAudioRequired", this.isBackgroundAudioRequired());
    }
}
