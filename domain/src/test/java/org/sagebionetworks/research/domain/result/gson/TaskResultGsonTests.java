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

package org.sagebionetworks.research.domain.result.gson;

import org.junit.*;
import org.sagebionetworks.research.domain.Schema;
import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class TaskResultGsonTests extends IndividualResultGsonTest {
    private static final Result EMPTY = new TaskResultBase("taskResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(35), UUID.fromString("1a17cac2-b430-484d-9f68-322ee6217592"),
            new Schema("schema", 1), new ArrayList<Result>(), new ArrayList<Result>());

    private static final Result TWO_STEP_HISTORY;
    static {
        List<Result> stepHistory = new ArrayList<>();
        stepHistory.add(new ResultBase("step1", Instant.ofEpochSecond(20), Instant.ofEpochSecond(30)));
        stepHistory.add(new ResultBase("step2", Instant.ofEpochSecond(30), null));
        TWO_STEP_HISTORY = new TaskResultBase("taskResult", Instant.ofEpochSecond(20),
                Instant.ofEpochSecond(35), UUID.fromString("1a17cac2-b430-484d-9f68-322ee6217592"),
                new Schema("schema", 1), stepHistory, new ArrayList<Result>());
    }

    private static final Result TWO_STEP_AND_ASYNC_HISTORY;
    static {
        List<Result> stepHistory = new ArrayList<>();
        stepHistory.add(new ResultBase("step1", Instant.ofEpochSecond(20), Instant.ofEpochSecond(30)));
        stepHistory.add(new ResultBase("step2", Instant.ofEpochSecond(30), null));
        List<Result> resultHistory = new ArrayList<>();
        resultHistory.add(new ResultBase("asyncResult1", Instant.ofEpochSecond(20),
                Instant.ofEpochSecond(31)));
        resultHistory.add(new ResultBase("asyncResult2", Instant.ofEpochSecond(31), null));
        TWO_STEP_AND_ASYNC_HISTORY = new TaskResultBase("taskResult", Instant.ofEpochSecond(20),
                Instant.ofEpochSecond(35), UUID.fromString("1a17cac2-b430-484d-9f68-322ee6217592"),
                new Schema("schema", 1), stepHistory, resultHistory);
    }

    @Test
    public void testTaskResult_Empty() {
        testCommon(EMPTY, "TaskResult_Empty.json");
    }

    @Test
    public void testTaskResult_TwoStepHistory() {
        testCommon(TWO_STEP_HISTORY, "TaskResult_TwoStepHistory.json");
    }

    @Test
    public void testTaskResult_TwoStepAndAsyncHistory() {
        testCommon(TWO_STEP_AND_ASYNC_HISTORY, "TaskResult_TwoStepAndAsyncHistory.json");
    }

    @Test
    public void testSerializeThenDeserialize_Empty() {
        this.testSerializationThenDeserialization(EMPTY);
    }

    @Test
    public void testSerializeThenDeserialize_TwoStepHistory() {
        this.testSerializationThenDeserialization(TWO_STEP_HISTORY);
    }

    @Test
    public void testSerializeThenDeserialize_TwoStepAndResultHistory() {
        this.testSerializationThenDeserialization(TWO_STEP_AND_ASYNC_HISTORY);
    }

    private void testCommon(Result expected, String filename) {
        Result result = this.readJsonFile(filename);
        assertNotNull(result);
        assertTrue(result instanceof TaskResult);
        assertEquals(expected, result);
    }
}
