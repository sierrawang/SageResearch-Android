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

package org.sagebionetworks.research.domain.step.gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sagebionetworks.research.domain.step.gson.UIStepGsonTest.UI_STEP_ASSERT_EQUALS;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.ActiveUIStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;

import java.util.function.BiConsumer;

public class ActiveUIStepGsonTest extends IndividualStepGsonTest {
    public static final BiConsumer<ActiveUIStep, ActiveUIStep> ACTIVE_UI_STEP_ASSERT_EQUALS
            = (@NonNull ActiveUIStep expected, @NonNull ActiveUIStep actual) -> {
        // inherited getters
        UI_STEP_ASSERT_EQUALS.accept(expected, actual);

        assertEquals(expected.getCommands(), actual.getCommands());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getSpokenInstructions(), actual.getSpokenInstructions());
        assertEquals(expected.isBackgroundAudioRequired(), actual.isBackgroundAudioRequired());
    };

    @Test
    public void testExample_1() {
        ActiveUIStep expected = mock(ActiveUIStep.class);
        when(expected.getIdentifier()).thenReturn("testActiveUIStep1");
        when(expected.getType()).thenReturn(StepType.ACTIVE);
        when(expected.getActions()).thenReturn(ImmutableMap.of());
        when(expected.getHiddenActions()).thenReturn(ImmutableSet.of());
        when(expected.getTitle()).thenReturn("title");
        when(expected.getText()).thenReturn("text");
        when(expected.getDetail()).thenReturn(null);
        when(expected.getFootnote()).thenReturn(null);
        when(expected.getCommands()).thenReturn(ImmutableSet.of());
        when(expected.getDuration()).thenReturn(5D);
        when(expected.getSpokenInstructions()).thenReturn(ImmutableMap.of());
        when(expected.isBackgroundAudioRequired()).thenReturn(false);

        Step rawStep = readStep("ActiveUIStep_1.json");
        assertTrue(rawStep instanceof ActiveUIStep);

        ActiveUIStep result = (ActiveUIStep) rawStep;
        UI_STEP_ASSERT_EQUALS.accept(expected, result);
    }

    @Test
    public void testExample_2() {

        ActiveUIStep expected = mock(ActiveUIStep.class);
        when(expected.getIdentifier()).thenReturn("testActiveUIStep2");
        when(expected.getType()).thenReturn(StepType.ACTIVE);
        when(expected.getActions()).thenReturn(ImmutableMap.of());
        when(expected.getHiddenActions()).thenReturn(ImmutableSet.of());
        when(expected.getTitle()).thenReturn("title");
        when(expected.getText()).thenReturn("text");
        when(expected.getDetail()).thenReturn("detail");
        when(expected.getFootnote()).thenReturn("footnote");
        when(expected.getCommands()).thenReturn(ImmutableSet.of());
        when(expected.getDuration()).thenReturn(5D);
        when(expected.getSpokenInstructions()).thenReturn(ImmutableMap.of());
        when(expected.isBackgroundAudioRequired()).thenReturn(false);

        Step rawStep = readStep("ActiveUIStep_2.json");
        assertTrue(rawStep instanceof ActiveUIStep);

        ActiveUIStep result = (ActiveUIStep) rawStep;
        UI_STEP_ASSERT_EQUALS.accept(expected, result);
    }

    @Test
    public void testExample_3() {
        ActiveUIStep expected = mock(ActiveUIStep.class);
        when(expected.getIdentifier()).thenReturn("testActiveUIStep3");
        when(expected.getType()).thenReturn(StepType.ACTIVE);
        when(expected.getActions()).thenReturn(ImmutableMap.of());
        when(expected.getHiddenActions()).thenReturn(ImmutableSet.of());
        when(expected.getTitle()).thenReturn("title");
        when(expected.getText()).thenReturn("text");
        when(expected.getDetail()).thenReturn("detail");
        when(expected.getFootnote()).thenReturn("footnote");
        when(expected.getCommands()).thenReturn(ImmutableSet.of());
        when(expected.getDuration()).thenReturn(5D);
        when(expected.getSpokenInstructions()).thenReturn(ImmutableMap.of());
        when(expected.isBackgroundAudioRequired()).thenReturn(false);

        Step rawStep = readStep("ActiveUIStep_3.json");
        assertTrue(rawStep instanceof ActiveUIStep);

        ActiveUIStep result = (ActiveUIStep) rawStep;
        UI_STEP_ASSERT_EQUALS.accept(expected, result);
    }
}
