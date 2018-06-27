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

package org.sagebionetworks.research.presentation.model.implementations;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import org.sagebionetworks.research.domain.step.interfaces.ActiveUIStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;
import org.sagebionetworks.research.presentation.model.ColorThemeView;
import org.sagebionetworks.research.presentation.model.ImageThemeView;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;
import org.threeten.bp.Duration;

public class ActiveUIStepViewBase extends UIStepViewBase implements ActiveUIStepView {
    public static final Creator<ActiveUIStepViewBase> CREATOR = new Creator<ActiveUIStepViewBase>() {
        @Override
        public ActiveUIStepViewBase createFromParcel(Parcel source) {
            return new ActiveUIStepViewBase(source);
        }

        @Override
        public ActiveUIStepViewBase[] newArray(int size) {
            return new ActiveUIStepViewBase[size];
        }
    };

    @NonNull
    private final Duration duration;

    private final boolean isBackgroundAudioRequired;

    public static ActiveUIStepViewBase fromActiveUIStep(@NonNull Step step, DrawableMapper mapper) {
        if (!(step instanceof ActiveUIStep)) {
            throw new IllegalArgumentException("Provided step: " + step + " is not an ActiveUIStep.");
        }

        ActiveUIStep activeUIStep = (ActiveUIStep) step;
        UIStepViewBase uiStepView = UIStepViewBase.fromUIStep(activeUIStep, mapper);
        // The duration from the ActiveUIStep is a Double in seconds, we convert this to a long of milliseconds and
        // round off any extra precision.
        Double millis = (activeUIStep.getDuration() != null ? activeUIStep.getDuration() : 0D) * 1000;
        Duration duration = Duration.ofMillis(millis.longValue());
        return new ActiveUIStepViewBase(uiStepView.getIdentifier(), uiStepView.getNavDirection(),
                uiStepView.getActions(), uiStepView.getTitle(), uiStepView.getText(), uiStepView.getDetail(),
                uiStepView.getFootnote(), uiStepView.getColorTheme(), uiStepView.getImageTheme(), duration,
                activeUIStep.isBackgroundAudioRequired());
    }

    public ActiveUIStepViewBase(@NonNull final String identifier, final int navDirection,
            @NonNull final ImmutableMap<String, ActionView> actions,
            @Nullable final DisplayString title,
            @Nullable final DisplayString text,
            @Nullable final DisplayString detail,
            @Nullable final DisplayString footnote,
            @Nullable final ColorThemeView colorTheme,
            @Nullable final ImageThemeView imageTheme, @NonNull final Duration duration,
            final boolean isBackgroundAudioRequired) {
        super(identifier, navDirection, actions, title, text, detail, footnote, colorTheme, imageTheme);
        this.duration = duration;
        this.isBackgroundAudioRequired = isBackgroundAudioRequired;
    }

    protected ActiveUIStepViewBase(Parcel in) {
        super(in);
        this.duration = (Duration) in.readSerializable();
        this.isBackgroundAudioRequired = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeSerializable(this.duration);
        dest.writeByte(this.isBackgroundAudioRequired ? (byte) 1 : (byte) 0);
    }

    @Override
    @NonNull
    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean isBackgroundAudioRequired() {
        return isBackgroundAudioRequired;
    }
}
