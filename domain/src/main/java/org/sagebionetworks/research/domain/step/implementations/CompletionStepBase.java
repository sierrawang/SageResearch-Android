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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.implementations.UIStepBase;
import org.sagebionetworks.research.domain.step.interfaces.CompletionStep;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;
import org.sagebionetworks.research.domain.step.ui.theme.ColorTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CompletionStepBase extends UIStepBase implements CompletionStep {
    public static final String TYPE_KEY = StepType.COMPLETION;
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionStepBase.class);

    // Default initializer for gson
    public CompletionStepBase() {
        super();
    }

    public CompletionStepBase(@NonNull final String identifier,
            @Nullable final Map<String, Action> actions,
            @Nullable final String title,
            @Nullable final String text,
            @Nullable final String detail,
            @Nullable final String footnote,
            @Nullable final ColorTheme colorTheme,
            @Nullable final ImageTheme imageTheme) {
        super(identifier, actions, title, text, detail, footnote, colorTheme, imageTheme);
    }

    @Override
    @NonNull
    public CompletionStepBase copyWithIdentifier(@NonNull String identifier) {
        CompletionStepBase result = new CompletionStepBase(identifier, this.getActions(), this.getTitle(), this.getText(), this.getDetail(),
                this.getFootnote(), this.getColorTheme(), this.getImageTheme());
        // If the user forgets to override copy with identifier, the type of the step will change when it goes through
        // the resource transformer. This is a really confusing bug so this code is present to make it clearer why
        // this is happening.
        if (result.getClass() != this.getClass()) {
            LOGGER.warn("Result of copy with identifier has different type than original input, did you"
                    + "forget to override CopyWithIdentifier");
        }

        return result;
    }

    @Override
    @NonNull
    public String getType() {
        return TYPE_KEY;
    }
}
