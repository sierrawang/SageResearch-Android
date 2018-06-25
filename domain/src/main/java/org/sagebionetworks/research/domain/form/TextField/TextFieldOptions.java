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

package org.sagebionetworks.research.domain.form.TextField;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * A TextFieldOptions is an object which stores information about a text field's settings such as the maximum number
 * of characters it can display and whether or not it is a secure text field.
 */
@AutoValue
public abstract class TextFieldOptions implements Parcelable {
    @AutoValue.Builder
    public static abstract class Builder {
        public abstract TextFieldOptions build();

        public abstract Builder setInvalidMessage(final String invalidMessage);

        public abstract Builder setMaximumLength(final int maximumLength);

        public abstract Builder setSecureTextEntry(final boolean isSecureTextEntry);

        public abstract Builder setTextValidator(final TextValidator textValidator);
    }

    public Builder builder() {
        return new AutoValue_TextFieldOptions.Builder();
    }

    /**
     * @return The message to display if the user's input is invalid.
     */
    public abstract String getInvalidMessage();

    /**
     * @return The maximum number of characters this text field is allowed to contain.
     */
    public abstract int getMaximumLength();

    /**
     * @return The text validator that corresponds to the text field.
     */
    public abstract TextValidator getTextValidator();

    /**
     * @return true if this should be a secure text entry (e.g. a password field), false otherwise
     */
    public abstract boolean isSecureTextEntry();
}
