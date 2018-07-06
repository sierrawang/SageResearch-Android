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

import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.interfaces.ThemedUIStep;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.ReminderAction;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.SkipToStepAction;
import org.sagebionetworks.research.presentation.ActionType;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;
import org.sagebionetworks.research.presentation.model.ColorThemeView;
import org.sagebionetworks.research.presentation.model.ImageThemeView;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.action.ActionViewBase;
import org.sagebionetworks.research.presentation.model.action.ReminderActionViewBase;
import org.sagebionetworks.research.presentation.model.action.SkipToStepActionViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UIStepViewBase implements UIStepView {
    public static final String TYPE = StepType.UI;

    public static final Creator<UIStepViewBase> CREATOR = new Creator<UIStepViewBase>() {
        @Override
        public UIStepViewBase createFromParcel(Parcel source) {
            return new UIStepViewBase(source);
        }

        @Override
        public UIStepViewBase[] newArray(int size) {
            return new UIStepViewBase[size];
        }
    };

    @NonNull
    private final ImmutableMap<String, ActionView> actions;

    @Nullable
    private final ColorThemeView colorTheme;

    @Nullable
    private final DisplayString detail;

    @Nullable
    private final DisplayString footnote;

    @NonNull
    private final String identifier;

    @Nullable
    private final ImageThemeView imageTheme;

    @NavDirection
    private final int navDirection;

    @Nullable
    private final DisplayString text;

    @Nullable
    private final DisplayString title;

    /**
     * Factory method for creating a UIStepViewBase from a ThemedUIStep.
     *
     * @param step
     *         The UIStep to create the UIStepViewBase from.
     * @return A UIStepViewBase created from the given ThemedUIStep.
     */
    public static UIStepViewBase fromUIStep(Step step, DrawableMapper mapper) {
        if (!(step instanceof ThemedUIStep)) {
            throw new IllegalArgumentException("Provided step: " + step + " is not a ThemedUIStep");
        }

        ThemedUIStep uiStep = (ThemedUIStep) step;
        String identifier = uiStep.getIdentifier();
        // TODO: rkolmos 05/29/2018 potentially change actions.
        ImmutableMap<String, ActionView> actions = ImmutableMap.copyOf(getActionsFrom(uiStep.getActions()));
        DisplayString title = DisplayString.create(null, uiStep.getTitle());
        DisplayString text = DisplayString.create(null, uiStep.getText());
        DisplayString detail = DisplayString.create(null, uiStep.getDetail());
        DisplayString footnote = DisplayString.create(null, uiStep.getFootnote());
        ColorThemeView colorTheme = ColorThemeView.fromColorTheme(uiStep.getColorTheme());
        ImageThemeView imageTheme = ImageThemeView.fromImageTheme(uiStep.getImageTheme(), mapper);
        // TODO: rkolmos 05/30/2018 for now the nav direction is always left.
        return new UIStepViewBase(identifier, NavDirection.SHIFT_LEFT, actions, title, text, detail, footnote,
                colorTheme, imageTheme);
    }

    /**
     * Constructor for creating a UIStepViewBase from the given information.
     *
     * @param identifier
     *         The unique identifier the UIStepViewBase should have.
     * @param actions
     *         The custom UI actions for the UIStepViewBase.
     * @param title
     *         The title the UIStepViewBase should have, fully localized.
     * @param text
     *         The text the UIStepViewBase should have, fully localized.
     * @param detail
     *         The detail the UIStepViewBase should have, fully localized.
     * @param footnote
     *         The footnote the UIStepViewBase should have, fully localized.
     * @param colorTheme
     *         The colorTheme the UIStepViewBase should use, with resources fully resolved.
     * @param imageTheme
     *         The imageTheme the UIStepViewBase should use, with resources fully resolved.
     */
    public UIStepViewBase(@NonNull final String identifier,
            final int navDirection, @NonNull final ImmutableMap<String, ActionView> actions,
            @Nullable final DisplayString title,
            @Nullable final DisplayString text, @Nullable final DisplayString detail,
            @Nullable final DisplayString footnote, @Nullable final ColorThemeView colorTheme,
            @Nullable final ImageThemeView imageTheme) {
        this.identifier = identifier;
        this.navDirection = navDirection;
        this.actions = actions;
        this.title = title;
        this.text = text;
        this.detail = detail;
        this.footnote = footnote;
        this.colorTheme = colorTheme;
        this.imageTheme = imageTheme;
    }

    protected UIStepViewBase(Parcel in) {
        this.identifier = in.readString();
        this.navDirection = in.readInt();
        this.actions = (ImmutableMap<String, ActionView>) in.readSerializable();
        this.title = in.readParcelable(DisplayString.class.getClassLoader());
        this.text = in.readParcelable(DisplayString.class.getClassLoader());
        this.detail = in.readParcelable(DisplayString.class.getClassLoader());
        this.footnote = in.readParcelable(DisplayString.class.getClassLoader());
        this.colorTheme = in.readParcelable(ColorThemeView.class.getClassLoader());
        this.imageTheme = in.readParcelable(ImageThemeView.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeInt(this.navDirection);
        dest.writeSerializable(this.actions);
        dest.writeParcelable(this.title, flags);
        dest.writeParcelable(this.text, flags);
        dest.writeParcelable(this.detail, flags);
        dest.writeParcelable(this.footnote, flags);
        dest.writeParcelable(this.colorTheme, flags);
        dest.writeParcelable(this.imageTheme, flags);
    }

    @Nullable
    @Override
    public ActionView getActionFor(@ActionType final String actionType) {
        // If we have an action from the json for the given actionType we use this, otherwise we return null.
        return this.actions.get(actionType);
    }

    @NonNull
    @Override
    public ImmutableMap<String, ActionView> getActions() {
        return this.actions;
    }

    @Nullable
    @Override
    public ColorThemeView getColorTheme() {
        return this.colorTheme;
    }

    @Nullable
    @Override
    public DisplayString getDetail() {
        return this.detail;
    }

    @Nullable
    @Override
    public DisplayString getFootnote() {
        return this.footnote;
    }

    @Nullable
    @Override
    public ImageThemeView getImageTheme() {
        return this.imageTheme;
    }

    @Nullable
    @Override
    public DisplayString getText() {
        return this.text;
    }

    @Nullable
    @Override
    public DisplayString getTitle() {
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
        return TYPE;
    }

    @Override
    public int getNavDirection() {
        return navDirection;
    }

    @Override
    public boolean shouldSkip(@Nullable final TaskResult taskResult) {
        return false;
    }

    protected static Map<String, ActionView> getActionsFrom(Map<String, Action> actions) {
        Map<String, ActionView> returnValue = new HashMap<>();
        for (Entry<String, Action> entry : actions.entrySet()) {
            String key = entry.getKey();
            Action action = entry.getValue();
            if (action instanceof ReminderAction) {
                returnValue.put(key, ReminderActionViewBase.fromReminderAction((ReminderAction) action));
            } else if (action instanceof SkipToStepAction) {
                returnValue.put(key, SkipToStepActionViewBase.fromSkipToStepAction((SkipToStepAction) action));
            } else {
                returnValue.put(key, ActionViewBase.fromAction(action));
            }
        }

        return returnValue;
    }
}
