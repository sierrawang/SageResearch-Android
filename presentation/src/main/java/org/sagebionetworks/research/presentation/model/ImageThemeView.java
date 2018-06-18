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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.domain.step.ui.theme.ColorPlacement;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;
import org.sagebionetworks.research.presentation.DisplayDrawable;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;

@AutoValue
public abstract class ImageThemeView implements Parcelable {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract ImageThemeView build();

        public abstract Builder setColorPlacement(@Nullable @ColorPlacement String colorPlacement);

        public abstract Builder setImageResource(@NonNull DisplayDrawable imageResource);
    }

    public static Builder builder() {
        return new AutoValue_ImageThemeView.Builder();
    }

    /**
     * Creates an ImageThemeView from an ImageTheme.
     *
     * @param imageTheme
     *         The image theme to create this imageThemeView from.
     * @return an ImageThemeView created from the given ImageTheme.
     */
    public static ImageThemeView fromImageTheme(@Nullable ImageTheme imageTheme) {
        if (imageTheme == null) {
            return null;
        }

        String imageName = imageTheme.getImageResourceName();
        return ImageThemeView.builder()
                .setColorPlacement(imageTheme.getColorPlacement())
                // There is no default image for the one displayed on the step's image view.
                .setImageResource(DisplayDrawable.create(null,
                        DrawableMapper.getDrawableFromName(imageName)))
                .build();
    }

    @Nullable
    @ColorPlacement
    public abstract String getColorPlacement();

    @NonNull
    public abstract DisplayDrawable getImageResource();
}
