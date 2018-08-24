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

package org.sagebionetworks.research.domain.recorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

import org.junit.Test;
import org.sagebionetworks.research.domain.JsonAssetUtil;
import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.async.DeviceMotionRecorderConfiguration;

public class MotionRecorderGsonTest {
    private static Gson GSON = DaggerRecorderTestComponent.builder().build().gson();

    @Test
    public void testMotionRecorderConfiguration_1() {
        AsyncActionConfiguration recorderConfiguration
                = JsonAssetUtil.readJsonFile(GSON, "recorder/MotionRecorder_1.json", AsyncActionConfiguration.class);
        assertTrue("Didn't deserialize as a DeviceMotionRecorderConfiguration",
                recorderConfiguration instanceof DeviceMotionRecorderConfiguration);
        DeviceMotionRecorderConfiguration dmrConfiguration
                = (DeviceMotionRecorderConfiguration) recorderConfiguration;
        assertEquals("Identifier " + dmrConfiguration.getIdentifier() + " doesn't equal expected motionRecorder1",
                "motionRecorder1", dmrConfiguration.getIdentifier());
        assertEquals("StartStepIdentifier " + dmrConfiguration.getStartStepIdentifier() + " doesn't equal expected "
                + "startStepIdentifier", "startStepIdentifier", dmrConfiguration.getStartStepIdentifier());
        assertEquals("StopStepIdentifier " + dmrConfiguration.getStopStepIdentifier() + " doesn't equal expected "
                + "stopStepIdentifier", "stopStepIdentifier", dmrConfiguration.getStopStepIdentifier());
        assertEquals(ImmutableSet.of("gravity", "gyro"), dmrConfiguration.getRecorderTypes());
        assertNull("Frequency " + dmrConfiguration.getFrequency() + "is non null", dmrConfiguration.getFrequency());
    }

    @Test
    public void testMotionRecorderConfiguration_2() {
        AsyncActionConfiguration recorderConfiguration
                = JsonAssetUtil.readJsonFile(GSON, "recorder/MotionRecorder_2.json", AsyncActionConfiguration.class);
        assertTrue("Didn't deserialize as a DeviceMotionRecorderConfiguration",
                recorderConfiguration instanceof DeviceMotionRecorderConfiguration);
        DeviceMotionRecorderConfiguration dmrConfiguration
                = (DeviceMotionRecorderConfiguration) recorderConfiguration;
        assertEquals("Identifier " + dmrConfiguration.getIdentifier() + " doesn't equal expected motionRecorderw",
                "motionRecorder2", dmrConfiguration.getIdentifier());
        assertNull("StartStepIdentifier " + dmrConfiguration.getStartStepIdentifier() + " is non null",
                dmrConfiguration.getStartStepIdentifier());
        assertNull("StopStepIdentifier " + dmrConfiguration.getStopStepIdentifier() + " is non null",
                dmrConfiguration.getStopStepIdentifier());
        assertEquals(ImmutableSet.of("gravity", "gyro"), dmrConfiguration.getRecorderTypes());
        assertEquals("Frequency " + dmrConfiguration.getFrequency() + "is not equal to expect frequency 3.0",
                Double.valueOf(3.0), dmrConfiguration.getFrequency());
    }
}
