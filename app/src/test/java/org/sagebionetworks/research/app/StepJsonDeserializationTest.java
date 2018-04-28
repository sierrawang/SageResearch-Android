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

import org.junit.*;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.ui.ActiveUIStep;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class StepJsonDeserializationTest {
    AppStepTestComponent stepTestComponent;

    @Before
    public void setup() {
        stepTestComponent = DaggerAppStepTestComponent.builder().build();
    }

    @Test
    public void testActiveUIStep() throws IOException {
        String id = "stepId";
        String type = "active";

        // TODO: add a test for null spokenInstructions once Immutable collection descrialization of nulls is fixed
        String json = getStringFromPath("step/active.json");
        Step step = stepTestComponent.gson().fromJson(json, Step.class);

        assertTrue(step instanceof ActiveUIStep);
    }

    @Test
    public void testInstructionStep() throws IOException {
        String id = "stepId";
        String type = "intruction";

        String json = getStringFromPath("step/instruction.json");
        Step step = stepTestComponent.gson().fromJson(json, Step.class);

        assertTrue(step instanceof InstructionStep);
    }

    private static String getStringFromPath(String fileName) throws IOException {
        ClassLoader classLoader = StepJsonDeserializationTest.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        File f = new File(resource.getPath());
        byte[] b = Files.readAllBytes(f.toPath());
        return new String(b);
    }
}