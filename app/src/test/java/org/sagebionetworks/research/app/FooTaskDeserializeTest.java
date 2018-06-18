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

package org.sagebionetworks.research.app;


import com.google.common.collect.ImmutableMap;

import org.junit.*;
import org.sagebionetworks.research.domain.step.interfaces.ActiveUIStep;
import org.sagebionetworks.research.domain.step.interfaces.FormUIStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.ui.action.implementations.ActionBase;
import org.sagebionetworks.research.domain.task.Task;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class FooTaskDeserializeTest extends JsonDeserializationTestBase {
    @Test
    public void testDeserializeFooTask() throws IOException {
        String json = getClasspathResourceAsString("task/foo.json");

        Task task = gson.fromJson(json, Task.class);

        assertNotNull(task);
        assertEquals("foo", task.getIdentifier());

        List<Step> steps = task.getSteps();

        assertEquals(7, steps.size());

        // TODO: test Step subclasses once implemented and assert remaining fields
        {
            assertThat(steps.get(0), instanceOf(InstructionStep.class));
            InstructionStep step1 = (InstructionStep) steps.get(0);
            assertEquals("step1", step1.getIdentifier());
            assertEquals("Step 1", step1.getTitle());
            assertEquals(
                    ImmutableMap.builder()
                            .put("goForward", ActionBase.builder()
                                    .setButtonTitle("Go, Dogs!")
                                    .build())
                            .build(),
                    step1.getActions());
        }
        {
            assertThat(steps.get(1), instanceOf(FormUIStep.class));
            FormUIStep step2 = (FormUIStep) steps.get(1);
            assertEquals("happiness", step2.getIdentifier());
            assertEquals("How happy are you?", step2.getTitle());
        }
        {
            Step step3 = steps.get(2);
            assertEquals("selectOne", step3.getIdentifier());
        }
        {
            Step step4 = steps.get(3);
            assertEquals("selectMultiple", step4.getIdentifier());
        }
        {
            assertThat(steps.get(4), instanceOf(InstructionStep.class));
            InstructionStep step8 = (InstructionStep) steps.get(4);
            assertEquals("step2", step8.getIdentifier());
            assertEquals(
                    ImmutableMap.builder()
                            .put("goForward", ActionBase.builder()
                                    .setButtonTitle("Start")
                                    .build())
                            .build(),
                    step8.getActions());
        }
        {
            Step step9 = steps.get(5);
            assertEquals("countdownStep", step9.getIdentifier());
        }
        {
            assertThat(steps.get(6), instanceOf(ActiveUIStep.class));
            ActiveUIStep step10 = (ActiveUIStep) steps.get(6);
            assertEquals("movingStep", step10.getIdentifier());
            assertEquals(Double.valueOf(20.0), step10.getDuration());
        }
    }
}
