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

package org.sagebionetworks.research.presentation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * A DisplayDrawable has two Drawable resources, a default and an override. When deciding which to display the
 * override takes presedence over the default. This allows for default behavior while still allowing this to get
 * overriden.
 */
@AutoValue
public abstract class DisplayDrawable implements Parcelable {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract DisplayDrawable build();

        public abstract Builder setDefaultDrawableRes(@Nullable @DrawableRes Integer defaultDrawableRes);

        public abstract Builder setDrawableRes(@Nullable @DrawableRes Integer drawableRes);
    }

    @Nullable
    @DrawableRes
    public abstract Integer getDefaultDrawableRes();

    @Nullable
    @DrawableRes
    public abstract Integer getDrawableRes();

    public static Builder builder() {
        return new AutoValue_DisplayDrawable.Builder();
    }

    public static DisplayDrawable create(@Nullable @DrawableRes Integer defaultDrawableRes,
            @Nullable @DrawableRes Integer drawableRes) {
        return DisplayDrawable.builder()
                .setDefaultDrawableRes(defaultDrawableRes)
                .setDrawableRes(drawableRes)
                .build();
    }

    /**
     * Returns the reference to the drawable that should be displayed for this DisplayDrawable, or null if no
     * reference could be resolved.
     * @return the reference to the drawable that should be displayed for this DisplayDrawable, or null if no
     * reference could be resolved.
     */
    @DrawableRes
    @Nullable
    public Integer getDrawable() {
        if (this.getDrawableRes() != null) {
            return this.getDrawableRes();
        } else {
            return this.getDefaultDrawableRes();
        }
    }

}
