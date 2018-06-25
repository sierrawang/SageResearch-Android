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

package org.sagebionetworks.research.presentation.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.ThemedUIStep;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;
import org.sagebionetworks.research.domain.step.ui.theme.ColorPlacement;
import org.sagebionetworks.research.domain.step.ui.theme.ColorTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.implementations.UIStepViewBase;

public class UIStepViewTests {
    public static final ThemedUIStep MOCK_UI_STEP;

    public static ColorTheme mockColorTheme(ImmutableMap<String, String> colorStyles, boolean isLightStyle) {
        ColorTheme theme = mock(ColorTheme.class);
        when(theme.isLightStyle()).thenReturn(isLightStyle);
        when(theme.getColorStyles()).thenReturn(colorStyles);
        return theme;
    }

    public static ImageTheme mockImageTheme(@ColorPlacement String colorPlacement, String imageResourceName) {
        ImageTheme theme = mock(ImageTheme.class);
        when(theme.getColorPlacement()).thenReturn(colorPlacement);
        return theme;
    }

    @Test
    public void testFromTUIStep() {
        UIStepViewBase result = UIStepViewBase.fromUIStep(MOCK_UI_STEP, null);
        assertNotNull(result);
        assertEquals("identifier", result.getIdentifier());
        assertEquals(DisplayString.create(null, "title"), result.getTitle());
        assertEquals(DisplayString.create(null, "text"), result.getText());
        assertEquals(DisplayString.create(null, "detail"), result.getDetail());
        assertEquals(DisplayString.create(null, "footnote"), result.getFootnote());
        ColorThemeView colorThemeView = result.getColorTheme();
        assertNotNull(colorThemeView);
        assertTrue(colorThemeView.lightStyle());
        ImageThemeView imageThemeView = result.getImageTheme();
        // The image theme view will be null since we have no drawable mapper.
        assertNull(imageThemeView);
    }

    protected static ThemedUIStep mockThemedUIStep(@NonNull final String identifier,
            @Nullable final ImmutableMap<String, Action> actions,
            @Nullable final String title, @Nullable final String text,
            @Nullable final String detail, @Nullable final String footnote,
            @Nullable final ColorTheme colorTheme,
            @Nullable final ImageTheme imageTheme) {
        ThemedUIStep step = mock(ThemedUIStep.class);
        when(step.getType()).thenReturn(StepType.UI);
        when(step.getIdentifier()).thenReturn(identifier);
        when(step.getActions()).thenReturn(actions);
        when(step.getTitle()).thenReturn(title);
        when(step.getText()).thenReturn(text);
        when(step.getDetail()).thenReturn(detail);
        when(step.getFootnote()).thenReturn(footnote);
        when(step.getColorTheme()).thenReturn(colorTheme);
        when(step.getImageTheme()).thenReturn(imageTheme);
        return step;
    }

    static {
        ImageTheme imageTheme = mockImageTheme(ColorPlacement.HEADER, null);
        ColorTheme colorTheme = mockColorTheme(null, true);
        MOCK_UI_STEP = mockThemedUIStep("identifier", ImmutableMap.of(), "title", "text",
                "detail", "footnote", colorTheme, imageTheme);
    }
}
