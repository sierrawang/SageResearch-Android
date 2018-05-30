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

public class DisplayDrawable implements Parcelable {
    @DrawableRes
    @Nullable
    public final Integer defaultDrawableRes;

    @DrawableRes
    @Nullable
    public final Integer drawableRes;


    public DisplayDrawable(@Nullable final Integer defaultDrawableRes, @Nullable final Integer drawableRes) {
        this.defaultDrawableRes = defaultDrawableRes;
        this.drawableRes = drawableRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.drawableRes);
        dest.writeValue(this.defaultDrawableRes);
    }

    protected DisplayDrawable(Parcel in) {
        this.drawableRes = (Integer) in.readValue(Integer.class.getClassLoader());
        this.defaultDrawableRes = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<DisplayDrawable> CREATOR = new Creator<DisplayDrawable>() {
        @Override
        public DisplayDrawable createFromParcel(Parcel source) {
            return new DisplayDrawable(source);
        }

        @Override
        public DisplayDrawable[] newArray(int size) {
            return new DisplayDrawable[size];
        }
    };
}
