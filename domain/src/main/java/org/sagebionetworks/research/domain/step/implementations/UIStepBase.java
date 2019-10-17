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

package org.sagebionetworks.research.domain.step.implementations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.ThemedUIStep;
import org.sagebionetworks.research.domain.step.ui.action.Action;
import org.sagebionetworks.research.domain.step.ui.theme.ColorTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UIStepBase extends StepBase implements ThemedUIStep {
    public static final String TYPE_KEY = StepType.UI;

    private static final Logger LOGGER = LoggerFactory.getLogger(UIStepBase.class);

    @NonNull
    private final ImmutableMap<String, Action> actions;

    @Nullable
    private final ColorTheme colorTheme;

    @Nullable
    private final String detail;

    @Nullable
    private final String footnote;

    @NonNull
    private final ImmutableSet<String> hiddenActions;

    @Nullable
    @SerializedName("image")
    private final ImageTheme imageTheme;

    @Nullable
    private final String text;

    @Nullable
    private final String title;

    // Gson initialize defaults
    protected UIStepBase() {
        super("", new HashSet<>());
        actions = ImmutableMap.of();
        hiddenActions = ImmutableSet.of();
        detail = null;
        footnote = null;
        text = null;
        title = null;
        colorTheme = null;
        imageTheme = null;
    }

    public UIStepBase(@NonNull final String identifier,
            @NonNull final Set<AsyncActionConfiguration> asyncActions,
            @Nullable final Map<String, Action> actions,
            @Nullable final Set<String> hiddenActions,
            @Nullable final String title, @Nullable final String text,
            @Nullable final String detail, @Nullable final String footnote,
            @Nullable final ColorTheme colorTheme,
            @Nullable final ImageTheme imageTheme) {
        super(identifier, asyncActions);
        this.hiddenActions = hiddenActions == null ? ImmutableSet.of() : ImmutableSet.copyOf(hiddenActions);
        this.colorTheme = colorTheme;
        this.imageTheme = imageTheme;
        if (actions == null) {
            this.actions = ImmutableMap.of();
        } else {
            this.actions = ImmutableMap.copyOf(actions);
        }
        this.title = title;
        this.text = text;
        this.detail = detail;
        this.footnote = footnote;
    }

    @NonNull
    @Override
    public UIStepBase copyWithIdentifier(@NonNull final String identifier) {
        UIStepBase result = copyWithIdentifierOperation(identifier);
        // If the user forgets to override copy with identifier, the type of the step will change when it goes through
        // the resource transformer. This is a really confusing bug so this code is present to make it clearer why
        // this is happening.
        if (result.getClass() != this.getClass()) {
            LOGGER.warn("Result of copy with identifier has different type than original input, did you"
                    + "forget to override CopyWithIdentifier");
        }

        return result;
    }

    @NonNull
    protected UIStepBase copyWithIdentifierOperation(@NonNull final String identifier) {
        return new UIStepBase(identifier, this.getAsyncActions(), this.getActions(), this.getHiddenActions(),
                this.getTitle(), this.getText(), this.getDetail(), this.getFootnote(), this.getColorTheme(),
                this.getImageTheme());
    }

    @NonNull
    @Override
    public ImmutableMap<String, Action> getActions() {
        return actions;
    }

    @NonNull
    @Override
    public ImmutableSet<String> getHiddenActions() {
        return hiddenActions;
    }

    @Nullable
    @Override
    public ColorTheme getColorTheme() {
        return this.colorTheme;
    }

    @Nullable
    @Override
    public ImageTheme getImageTheme() {
        return this.imageTheme;
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
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        UIStepBase uiStep = (UIStepBase) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.getActions(), uiStep.getActions())
                && Objects.equal(this.getTitle(), uiStep.getTitle())
                && Objects.equal(this.getText(), uiStep.getText())
                && Objects.equal(this.getDetail(), uiStep.getDetail())
                && Objects.equal(this.getFootnote(), uiStep.getFootnote())
                && Objects.equal(this.getColorTheme(), uiStep.getColorTheme())
                && Objects.equal(this.getImageTheme(), uiStep.getImageTheme());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.actions, this.detail, this.footnote, this.text, this.title,
                        this.colorTheme, this.imageTheme);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("actions", actions)
                .add("identifier", this.getIdentifier())
                .add("type", this.getType())
                .add("title", this.getTitle())
                .add("text", this.getText())
                .add("detail", this.getDetail())
                .add("footnote", this.getFootnote())
                .add("colorTheme", this.getColorTheme())
                .add("imageTheme", this.getImageTheme());
    }
                           }
