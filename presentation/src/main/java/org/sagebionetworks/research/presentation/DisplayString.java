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

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.auto.value.AutoValue;

import java.io.Serializable;

/**
 * A DisplayString has a default string resource and an override string. When deciding which String to display the
 * override string takes precedence over the default resource. This allows for default behavior while still providing
 * the option to override this behavior.
 */
@AutoValue
public abstract class DisplayString implements Serializable {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract DisplayString build();

        public abstract Builder setDefaultDisplayStringRes(@Nullable @StringRes Integer defaultDisplayStringRes);

        public abstract Builder setDisplayString(@Nullable String displayString);
    }

    public static Builder builder() {
        return new AutoValue_DisplayString.Builder();
    }

    public static DisplayString create(@Nullable @StringRes Integer defaultRes, @Nullable String displayString) {
        return DisplayString.builder()
                .setDefaultDisplayStringRes(defaultRes)
                .setDisplayString(displayString)
                .build();
    }

    @Nullable
    @StringRes
    public abstract Integer getDefaultDisplayStringRes();

    @Nullable
    public abstract String getDisplayString();

    /**
     * Returns the String that should be displayed for this DisplayString, or null if neither the override string nor
     * the default resource id is usable.
     *
     * @param resources
     *         The resources to resolve the default string res from if necessary.
     * @return the String that should be displayed for this DisplayString. or null if neither the override string nor
     * the defualt resource id is usable.
     */
    @Nullable
    public String getString(Resources resources) {
        if (this.getDisplayString() != null) {
            return this.getDisplayString();
        } else if (this.getDefaultDisplayStringRes() != null) {
            return resources.getString(this.getDefaultDisplayStringRes());
        }

        return null;
    }
}
