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

import org.sagebionetworks.research.domain.step.interfaces.ThemedUIStep;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.ColorThemeView;
import org.sagebionetworks.research.presentation.model.ImageThemeView;
import org.sagebionetworks.research.presentation.model.UIActionView;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;

public class UIStepViewBase implements UIStepView {
    @NonNull
    private final String identifier;
    @NavDirection
    private final int navDirection;
    @NonNull
    private final ImmutableMap<String, UIActionView> actions;
    @Nullable
    private final DisplayString title;
    @Nullable
    private final DisplayString text;
    @Nullable
    private final DisplayString detail;
    @Nullable
    private final DisplayString footnote;
    @Nullable
    private final ColorThemeView colorTheme;
    @Nullable
    private final ImageThemeView imageTheme;

    /**
     * Factory method for creating a UIStepViewBase from a ThemedUIStep.
     * @param step The UIStep to create the UIStepViewBase from.
     * @return A UIStepViewBase created from the given ThemedUIStep.
     */
    public static UIStepViewBase fromUIStep(ThemedUIStep step) {
        String identifier = step.getIdentifier();
        // TODO: rkolmos 05/29/2018 potentially change actions.
        ImmutableMap<String, UIActionView> actions = UIActionView.getActionsFrom(step.getActions());
        // TODO: rkolmos 05/29/2018 Localize Strings.
        DisplayString title = new DisplayString(0, step.getTitle());
        DisplayString text = new DisplayString(0, step.getText());
        DisplayString detail = new DisplayString(0, step.getDetail());
        DisplayString footnote = new DisplayString(0, step.getFootnote());

        ColorThemeView colorTheme = ColorThemeView.fromColorTheme(step.getColorTheme());
        ImageThemeView imageTheme = ImageThemeView.fromImageTheme(step.getImageTheme());
        // TODO: rkolmos 05/30/2018 for now the nav direction is always left.
        return new UIStepViewBase(identifier, NavDirection.SHIFT_LEFT, actions, title, text, detail, footnote,
                colorTheme, imageTheme);
    }

    /**
     * Constructor for creating a UIStepViewBase from the given information.
     * @param identifier The unique identifier the UIStepViewBase should have.
     * @param navDirection
     * @param actions The custom UI actions for the UIStepViewBase.
     * @param title The title the UIStepViewBase should have, fully localized.
     * @param text The text the UIStepViewBase should have, fully localized.
     * @param detail The detail the UIStepViewBase should have, fully localized.
     * @param footnote The footnote the UIStepViewBase should have, fully localized.
     * @param colorTheme The colorTheme the UIStepViewBase should use, with resources fully resolved.
     * @param imageTheme The imageTheme the UIStepViewBase should use, with resources fully resolved.
     */
    public UIStepViewBase(@NonNull final String identifier,
            final int navDirection, @NonNull final ImmutableMap<String, UIActionView> actions,
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


    @NonNull
    @Override
    public String getIdentifier() {
        return this.identifier;
    }


    @Override
    public int getNavDirection() {
        return navDirection;
    }

    @Nullable
    @Override
    public DisplayString getTitle() {
        return this.title;
    }

    @Nullable
    @Override
    public DisplayString getText() {
        return this.text;
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

    @NonNull
    @Override
    public ImmutableMap<String, UIActionView> getActions() {
        return this.actions;
    }

    @Nullable
    @Override
    public ColorThemeView getColorTheme() {
        return this.colorTheme;
    }

    @Nullable
    @Override
    public ImageThemeView getImageTheme() {
        return this.imageTheme;
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

    protected UIStepViewBase(Parcel in) {
        this.identifier = in.readString();
        this.navDirection = in.readInt();
        this.actions = (ImmutableMap<String, UIActionView>) in.readSerializable();
        this.title = in.readParcelable(DisplayString.class.getClassLoader());
        this.text = in.readParcelable(DisplayString.class.getClassLoader());
        this.detail = in.readParcelable(DisplayString.class.getClassLoader());
        this.footnote = in.readParcelable(DisplayString.class.getClassLoader());
        this.colorTheme = in.readParcelable(ColorThemeView.class.getClassLoader());
        this.imageTheme = in.readParcelable(ImageThemeView.class.getClassLoader());
    }

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
}
