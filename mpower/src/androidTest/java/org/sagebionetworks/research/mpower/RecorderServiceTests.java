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

package org.sagebionetworks.research.mpower;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.research.mobile_ui.recorder.Recorder;
import org.sagebionetworks.research.mobile_ui.recorder.RecorderActionType;
import org.sagebionetworks.research.mobile_ui.recorder.RecorderManager;
import org.sagebionetworks.research.mobile_ui.recorder.RecorderService;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class RecorderServiceTests {
    /**
     * Creates and returns an Intent that can be given to the RecorderService to perform the given actionType
     * on the given recorder.
     * @param recorder The recorder to perform the given actionType on.
     * @param actionType The type of action to perform on the given recorder.
     * @return an Intent that can be given to the RecorderService to perform the given actionType on the given
     *         recorder.
     */
    public static Intent createRecorderIntent(Context context, Recorder recorder, @RecorderActionType String actionType) {
        Intent intent = new Intent(context, RecorderService.class);
        intent.putExtra(RecorderService.RECORDER_KEY, recorder);
        intent.putExtra(RecorderService.ACTION_KEY, actionType);
        return intent;
    }

    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    @Test
    public void testStartService() throws TimeoutException {
        Context context = InstrumentationRegistry.getContext();
        TestRecorder recorder = new TestRecorder(null, null);
        Intent intent = createRecorderIntent(context, recorder, RecorderActionType.START);
        this.serviceRule.startService(intent);
    }

    @Test
    public void testStartRecorder() throws TimeoutException {
        Context context = InstrumentationRegistry.getContext();
        TestRecorder recorder = new TestRecorder(null, null);
        Intent intent = createRecorderIntent(context, recorder, RecorderActionType.START);
        this.serviceRule.startService(intent);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // This shouldn't happen
        }

        assertTrue(recorder.isStartCalled());
   }

    @Test
    public void testStopRecorder() throws TimeoutException {
        Context context = InstrumentationRegistry.getContext();
        TestRecorder recorder = new TestRecorder(null, null);
        Intent intent = createRecorderIntent(context, recorder, RecorderActionType.STOP);
        this.serviceRule.startService(intent);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // This shouldn't happen
        }

        assertTrue(recorder.isStopCalled());
    }

    @Test
    public void testCancelRecorder() throws TimeoutException {
        Context context = InstrumentationRegistry.getContext();
        TestRecorder recorder = new TestRecorder(null, null);
        Intent intent = createRecorderIntent(context, recorder, RecorderActionType.CANCEL);
        this.serviceRule.startService(intent);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // This shouldn't happen
        }

        assertTrue(recorder.isCancelCalled());
    }
}
