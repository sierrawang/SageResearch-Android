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
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.common.base.Objects;


public class DisplayString implements Parcelable {
    // resource id for string to display
    @StringRes
    @Nullable
    public final Integer defaultDisplayStringRes;

    // string to display, overrides defaultDisplayStringRes
    @Nullable
    public final String displayString;

    public DisplayString(@StringRes final Integer defaultDisplayStringRes, @Nullable final String displayString) {
        this.defaultDisplayStringRes = defaultDisplayStringRes;
        this.displayString = displayString;
    }

    protected DisplayString(Parcel in) {
        defaultDisplayStringRes = in.readInt();
        displayString = in.readString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DisplayString that = (DisplayString) o;
        return Objects.equal(defaultDisplayStringRes, that.defaultDisplayStringRes) &&
                Objects.equal(displayString, that.displayString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(defaultDisplayStringRes, displayString);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(defaultDisplayStringRes);
        dest.writeString(displayString);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DisplayString> CREATOR = new Creator<DisplayString>() {
        @Override
        public DisplayString createFromParcel(Parcel in) {
            return new DisplayString(in);
        }

        @Override
        public DisplayString[] newArray(int size) {
            return new DisplayString[size];
        }
    };
}
