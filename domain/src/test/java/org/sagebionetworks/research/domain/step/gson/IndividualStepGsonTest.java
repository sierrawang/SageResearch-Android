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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.*;
import org.sagebionetworks.research.domain.step.DaggerStepTestComponent;
import org.sagebionetworks.research.domain.step.StepTestComponent;
import org.sagebionetworks.research.domain.step.interfaces.Step;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;

import static junit.framework.Assert.assertNotNull;

public abstract class IndividualStepGsonTest {
    protected StepTestComponent stepTestComponent;

    @Before
    public void setup() {
        this.stepTestComponent = DaggerStepTestComponent.builder().build();
    }

    @Nullable
    private Step readJsonFileHelper(URL url) {
        try {
            Reader reader = new FileReader(new File(url.getFile()));
            Step step = this.stepTestComponent.gson().fromJson(reader, Step.class);
            return step;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @NonNull
    protected Step readJsonFile(String filename) {
        ClassLoader loader = this.getClass().getClassLoader();
        URL url = loader.getResource("steps/" + filename);
        Step step = this.readJsonFileHelper(url);
        assertNotNull("Failed to read file " + filename, step);
        return step;
    }

}
