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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.interfaces.UIStep;
import org.sagebionetworks.research.domain.step.ui.action.implementations.ActionBase;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;

public class UIStepGsonTests extends IndividualStepGsonTest {
    @Test
    public void testExample_1() {
        UIStep expected = mock(UIStep.class);
        when(expected.getIdentifier()).thenReturn("testUIStep1");
        when(expected.getActions()).thenReturn(ImmutableMap.<String, Action>of());
        when(expected.getTitle()).thenReturn("title");
        when(expected.getText()).thenReturn("text");
        when(expected.getDetail()).thenReturn(null);
        when(expected.getFootnote()).thenReturn(null);

        testCommon(expected, "UIStep_1.json");
    }

    @Test
    public void testExample_2() {
        UIStep expected = mock(UIStep.class);
        when(expected.getIdentifier()).thenReturn("testUIStep2");
        when(expected.getActions()).thenReturn(ImmutableMap.<String, Action>builder()
                .put("goForward", ActionBase.builder().setButtonTitle("Go, Dogs!").build()).build());
        when(expected.getTitle()).thenReturn("title");
        when(expected.getText()).thenReturn("text");
        when(expected.getDetail()).thenReturn("detail");
        when(expected.getFootnote()).thenReturn("footnote");

        testCommon(expected, "UIStep_2.json");
    }

    private void testCommon(UIStep expected, String filename) {
        Step step = this.readJsonFile(filename);
        assertNotNull(step);
        assertTrue(step instanceof UIStep);
        UIStep uistep = (UIStep) step;
        assertEquals(expected.getActions(), uistep.getActions());
        assertEquals(expected.getDetail(), uistep.getDetail());
        assertEquals(expected.getFootnote(), uistep.getFootnote());
        assertEquals(expected.getText(), uistep.getText());
        assertEquals(expected.getTitle(), uistep.getTitle());
    }
}