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

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.domain.step.ui.theme.AnimationImageTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ColorPlacement;
import org.sagebionetworks.research.presentation.DisplayDrawable;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class AnimationImageThemeView extends ImageThemeView {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract AnimationImageThemeView build();

        public abstract Builder setColorPlacement(@ColorPlacement String colorPlacement);

        public abstract Builder setImageResources(@Nullable List<DisplayDrawable> imageResources);

        public abstract Builder setDuration(@Nullable Double duration);
    }

    public abstract List<DisplayDrawable> getImageResources();

    public abstract Double getDuration();

    public static Builder builder() {
        return new AutoValue_AnimationImageThemeView.Builder();
    }

    public static AnimationImageThemeView fromAnimationImageTheme(AnimationImageTheme imageTheme,
            DrawableMapper mapper) {
        List<DisplayDrawable> imageResources = new ArrayList<>();
        for (String id : imageTheme.getImageResourceNames()) {
            imageResources.add(DisplayDrawable.create(null, mapper.getDrawableFromName(id)));
        }

        return AnimationImageThemeView.builder()
                .setColorPlacement(imageTheme.getColorPlacement())
                .setDuration(imageTheme.getDuration())
                .setImageResources(imageResources)
                .build();

    }
}
