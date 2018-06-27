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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.research.domain.step.interfaces.ActiveUIStep;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.interfaces.UIStep;

import java.util.List;

public class SectionStepGsonTests extends IndividualStepGsonTest {
    @Test
    public void testEmpty() {
        Step step = this.readJsonFile("EmptySectionStep.json");
        assertNotNull(step);
        assertTrue(step instanceof SectionStep);
        SectionStep sectionStep = (SectionStep) step;
        assertEquals("emptySectionStep", sectionStep.getIdentifier());
        assertTrue(sectionStep.getSteps().isEmpty());
    }

    @Test
    public void testExample_1() {
        Step step = this.readJsonFile("SectionStep_1.json");
        assertNotNull(step);
        assertTrue(step instanceof SectionStep);
        SectionStep sectionStep = (SectionStep) step;
        assertEquals("testSectionStep1", sectionStep.getIdentifier());
        List<Step> steps = sectionStep.getSteps();
        assertNotNull(steps);
        assertEquals(2, steps.size());
        Step substep1 = steps.get(0);
        assertTrue(substep1 instanceof UIStep);
        Step substep2 = steps.get(1);
        assertTrue(substep2 instanceof ActiveUIStep);
    }
}
