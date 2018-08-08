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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.research.domain.result.implementations.CollectionResultBase;
import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.result.interfaces.CollectionResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;

public class CollectionResultGsonTest extends IndividualResultGsonTest {
    private static final Result EMPTY = new CollectionResultBase("collectionResult",
            Instant.ofEpochSecond(20), Instant.ofEpochSecond(30), new ArrayList<Result>());

    private static final Result SINGLE;

    private static final Result DOUBLE;

    @Test
    public void testCollectionResult_Empty() {
        testCommon(EMPTY, "CollectionResult_Empty.json");
    }

    @Test
    public void testCollectionResult_SingleInputResult() {
        testCommon(SINGLE, "CollectionResult_SingleInputResult.json");
    }

    @Test
    public void testCollectionResult_TwoInputResults() {
        testCommon(DOUBLE, "CollectionResult_TwoInputResults.json");
    }

    @Test
    public void testSerializationDeserialzation_Empty() {
        testSerializationThenDeserialization(EMPTY);
    }

    @Test
    public void testSerializationDeserialzation_SingleInputField() {
        testSerializationThenDeserialization(SINGLE);
    }

    @Test
    public void testSerializationDeserialzation_TwoInputFields() {
        testSerializationThenDeserialization(DOUBLE);
    }

    static {
        List<Result> expectedSubResults = new ArrayList<Result>();
        expectedSubResults.add(new ResultBase("subResult1", Instant.ofEpochSecond(20), null));
        SINGLE = new CollectionResultBase("collectionResult", Instant.ofEpochSecond(20),
                Instant.ofEpochSecond(30), expectedSubResults);
    }

    static {
        List<Result> expectedSubResults = new ArrayList<Result>();
        expectedSubResults.add(new ResultBase("subResult1", Instant.ofEpochSecond(20),
                Instant.ofEpochSecond(25)));
        expectedSubResults.add(new ResultBase("subResult2", Instant.ofEpochSecond(25), null));
        DOUBLE = new CollectionResultBase("collectionResult", Instant.ofEpochSecond(20),
                Instant.ofEpochSecond(30), expectedSubResults);
    }
}
