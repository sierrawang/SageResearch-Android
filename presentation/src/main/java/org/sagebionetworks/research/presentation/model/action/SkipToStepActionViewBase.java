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

package org.sagebionetworks.research.presentation.model.action;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.domain.step.ui.action.SkipToStepAction;
import org.sagebionetworks.research.presentation.DisplayDrawable;
import org.sagebionetworks.research.presentation.DisplayString;

import javax.annotation.Nullable;

@AutoValue
public abstract class SkipToStepActionViewBase implements SkipToStepActionView {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract SkipToStepActionViewBase build();

        public abstract Builder setButtonIcon(@Nullable DisplayDrawable buttonIcon);

        public abstract Builder setButtonTitle(@Nullable DisplayString buttonTitle);

        public abstract Builder setSkipToStepIdentifier(@Nullable String skipToStepIdentifier);
    }

    public static Builder builder() {
        return new AutoValue_SkipToStepActionViewBase.Builder();
    }

    public static SkipToStepActionViewBase fromSkipToStepAction(SkipToStepAction skipToStepAction) {
        // TODO rkolmos 06/07/2018 add the icon to this method.
        return SkipToStepActionViewBase.builder()
                .setButtonTitle(DisplayString.create(null, skipToStepAction.getButtonTitle()))
                .setSkipToStepIdentifier(skipToStepAction.getSkipToStepIdentifier())
                .build();
    }
}
