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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

import org.sagebionetworks.research.domain.step.ui.UIStep;

public class UIStepBase implements UIStep {
    public static final String TYPE_KEY = "ui";

    @NonNull
    private final String identifier;
    @Nullable
    private final String title;
    @Nullable
    private final String text;
    @Nullable
    private final String detail;
    @Nullable
    private final String footnote;

    public UIStepBase(@NonNull final String identifier,
            @Nullable final String title, @Nullable  final String text,
            @Nullable final String detail, @Nullable final String footnote) {
        this.identifier = identifier;
        this.title = title;
        this.text = text;
        this.detail = detail;
        this.footnote = footnote;
    }

    @NonNull
    @Override
    public String getIdentifier() {
        return this.identifier;
    }


    @NonNull
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Nullable
    @Override
    public String getTitle() {
        return this.title;
    }

    @Nullable
    @Override
    public String getText() {
        return this.text;
    }

    @Nullable
    @Override
    public String getDetail() {
        return this.detail;
    }

    @Nullable
    @Override
    public String getFootnote() {
        return this.footnote;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && this.getClass() == o.getClass() && this.equalsHelper(o));
    }

    @Override
    public String toString() {
        return this.toStringHelper().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier, this.title, this.text, this.detail, this.footnote);
    }

    /**
     * Returns true if all the UIStepBase fields of this object are equal to all the UIStepBase fields of o,
     * false otherwise. It is expected that subclasses will override this to add their fields to the comparison.
     * Requires: this.getClass() == o.getClass()
     * @param o The object to check for equality with this.
     * @return True if all the UIStepBase fields of this object are equal to all the UIStepBase fields of o,
     *      false otherwise.
     */
    protected boolean equalsHelper(Object o) {
        UIStepBase uiStep = (UIStepBase) o;
        return Objects.equal(this.identifier, uiStep.identifier) &&
                Objects.equal(this.title, uiStep.title) &&
                Objects.equal(this.text, uiStep.text) &&
                Objects.equal(this.detail, uiStep.detail) &&
                Objects.equal(this.footnote, uiStep.footnote);
    }

    /**
     * Returns the ToStringHelper that can be used to create the toString() representation of this as a UIStepBase
     * object. It is expected that subclasses will override this to add their own fields to the toString().
     * @return The toStringHelper for this UIStepBase.
     */
    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", this.getIdentifier())
                .add("type", this.getType())
                .add("title", this.getTitle())
                .add("text", this.getText())
                .add("detail", this.getDetail())
                .add("footnote", this.getFootnote());
    }
}
