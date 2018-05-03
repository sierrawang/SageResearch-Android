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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.sagebionetworks.research.domain.step.ui.UIAction;
import org.sagebionetworks.research.domain.step.ui.UIStep;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class UIStepBase implements UIStep {
    public static final String TYPE_KEY = "ui";

    @NonNull
    private final ImmutableMap<String, UIAction> actions;

    @Nullable
    private final String detail;

    @Nullable
    private final String footnote;

    @NonNull
    private final String identifier;

    @Nullable
    private final String text;

    @Nullable
    private final String title;

    // Gson initialize defaults
    UIStepBase() {
        actions = ImmutableMap.of();
        detail = null;
        footnote = null;
        identifier = "";
        text = null;
        title = null;
    }

    public UIStepBase(@NonNull final String identifier, @NonNull final Map<String, UIAction> actions,
            @Nullable final String title, @Nullable final String text,
            @Nullable final String detail, @Nullable final String footnote) {
        checkArgument(!Strings.isNullOrEmpty(identifier));
        checkNotNull(actions);

        this.identifier = identifier;
        this.actions = ImmutableMap.copyOf(actions);
        this.title = title;
        this.text = text;
        this.detail = detail;
        this.footnote = footnote;
    }

    @NonNull
    @Override
    public ImmutableMap<String, UIAction> getActions() {
        return actions;
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

    @Nullable
    @Override
    public String getText() {
        return this.text;
    }

    @Nullable
    @Override
    public String getTitle() {
        return this.title;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(actions, detail, footnote, identifier, text, title);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UIStepBase that = (UIStepBase) o;
        return equalsHelper(o);
    }

    @Override
    public String toString() {
        return toStringHelper()
                .toString();
    }

    /**
     * Returns true if all the UIStepBase fields of this object are equal to all the UIStepBase fields of o, false
     * otherwise. It is expected that subclasses will override this to add their fields to the comparison. Requires:
     * this.getClass() == o.getClass()
     *
     * @param o
     *         The object to check for equality with this.
     * @return True if all the UIStepBase fields of this object are equal to all the UIStepBase fields of o, false
     * otherwise.
     */
    protected boolean equalsHelper(Object o) {
        UIStepBase uiStep = (UIStepBase) o;
        return Objects.equal(actions, uiStep.actions) &&
                Objects.equal(this.identifier, uiStep.identifier) &&
                Objects.equal(this.title, uiStep.title) &&
                Objects.equal(this.text, uiStep.text) &&
                Objects.equal(this.detail, uiStep.detail) &&
                Objects.equal(this.footnote, uiStep.footnote);
    }

    /**
     * Returns the ToStringHelper that can be used to create the toString() representation of this as a UIStepBase
     * object. It is expected that subclasses will override this to add their own fields to the toString().
     *
     * @return The toStringHelper for this UIStepBase.
     */
    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("actions", actions)
                .add("identifier", this.getIdentifier())
                .add("type", this.getType())
                .add("title", this.getTitle())
                .add("text", this.getText())
                .add("detail", this.getDetail())
                .add("footnote", this.getFootnote());
    }
}
