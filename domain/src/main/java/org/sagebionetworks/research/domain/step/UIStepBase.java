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

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import java.util.Map;
import org.sagebionetworks.research.domain.step.ui.UIAction;
import org.sagebionetworks.research.domain.step.ui.UIActionType;
import org.sagebionetworks.research.domain.step.ui.UIStep;

/**
 * Created by liujoshua on 10/13/2017.
 */

public class UIStepBase extends StepBase implements UIStep {
    @StringRes
    private final int title;

    @StringRes
    private final int text;

    @StringRes
    private final int detail;

    @StringRes
    private final int footnote;

    @DrawableRes
    private final int imageBefore;

    @DrawableRes
    private final int imageAfter;

    @NonNull
    private final Map<String, UIAction> uiActions;

    public UIStepBase(@NonNull String identifier, @NonNull String type, @StringRes int title, @StringRes int text,
        @StringRes int detail, @StringRes int footnote, @DrawableRes int imageBefore, @DrawableRes int imageAfter,
        @NonNull Map<String, UIAction> uiActions) {
        super(identifier, type);
        checkNotNull(uiActions);

        this.title = title;
        this.text = text;
        this.detail = detail;
        this.footnote = footnote;
        this.imageBefore = imageBefore;
        this.imageAfter = imageAfter;
        this.uiActions = uiActions;
    }

    public UIAction getAction(@NonNull @UIActionType String actionType) {
        return uiActions.get(actionType);
    }

    public boolean shouldHideAction(@NonNull @UIActionType String actionType) {
        return uiActions.get(actionType) == null;
    }

    @Override
    public int getTitle() {
        return title;
    }

    @Override
    public int getText() {
        return text;
    }

    @Override
    public int getDetail() {
        return detail;
    }

    @Override
    public int getFootnote() {
        return footnote;
    }
}
