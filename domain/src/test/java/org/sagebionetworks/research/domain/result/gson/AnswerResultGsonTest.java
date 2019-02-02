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

import org.junit.Test;
import org.sagebionetworks.research.domain.result.AnswerResultType;
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.threeten.bp.Instant;

public class AnswerResultGsonTest extends IndividualResultGsonTest {
    private static final Result INTEGER = new AnswerResultBase<>("answerResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(30), 1, AnswerResultType.INTEGER);

    private static final Result STRING = new AnswerResultBase<>("answerResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(30), "This is a string", AnswerResultType.STRING);

    private static final Result BOOLEAN = new AnswerResultBase<>("answerResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(30), true, AnswerResultType.BOOLEAN);

    private static final Result DOUBLE = new AnswerResultBase<>("answerResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(30), 2.0, AnswerResultType.DECIMAL);

    @Test
    public void testAnswerResult_Boolean() {
        testCommon(BOOLEAN, "AnswerResult_Boolean.json");
    }

    @Test
    public void testAnswerResult_Double() {
        testCommon(DOUBLE, "AnswerResult_Double.json");
    }

    @Test
    public void testAnswerResult_Integer() {
        testCommon(INTEGER, "AnswerResult_Integer.json");
    }

    @Test
    public void testAnswerResult_String() {
        testCommon(STRING, "AnswerResult_String.json");
    }

    @Test
    public void testSerializationDeserializationIntegration_Boolean() {
        testSerializationThenDeserialization(BOOLEAN);
    }

    @Test
    public void testSerializationDeserializationIntegration_Double() {
        testSerializationThenDeserialization(DOUBLE);
    }

    @Test
    public void testSerializationDeserializationIntegration_Integer() {
        testSerializationThenDeserialization(INTEGER);
    }

    @Test
    public void testSerializationDeserializationIntegration_String() {
        testSerializationThenDeserialization(STRING);
    }
}
