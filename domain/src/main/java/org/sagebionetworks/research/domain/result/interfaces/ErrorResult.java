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

package org.sagebionetworks.research.domain.result.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Objects;

import org.threeten.bp.Instant;

import java.util.Arrays;

/**
 * An ErrorResult is used to indicate that an error occurred. It allows for an error description and a Throwable to be
 * set to describe the error that occurred.
 */
public interface ErrorResult extends Result {
    class ErrorResultThrowable extends Throwable {
        public ErrorResultThrowable(String message) {
            super(message);
        }

        public ErrorResultThrowable(String message, Throwable cause) {
            super(message, cause);
        }

        public ErrorResultThrowable(Throwable cause) {
            super(cause);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getMessage(), Arrays.hashCode(getStackTrace()),
                    Arrays.hashCode(getSuppressed()));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ErrorResultThrowable t = (ErrorResultThrowable) o;
            return Objects.equal(getMessage(), t.getMessage())
                    && Arrays.equals(getStackTrace(), t.getStackTrace())
                    && Arrays.equals(getSuppressed(), t.getSuppressed());
        }
    }

    /**
     * @return The time this result ended.
     */
    @Override
    @NonNull
    Instant getEndTime();

    /**
     * @return A description of the Error that this result represents.
     */
    @NonNull
    String getErrorDescription();

    /**
     * @return The throwable corresponding to the Error that this result represents.
     */
    @Nullable
    ErrorResultThrowable getThrowable();
}
