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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.sagebionetworks.research.domain.JsonAssetUtil.readJsonFile;

import org.junit.Test;
import org.sagebionetworks.research.domain.result.implementations.ErrorResultBase;
import org.sagebionetworks.research.domain.result.interfaces.ErrorResult;
import org.sagebionetworks.research.domain.result.interfaces.ErrorResult.ErrorResultThrowable;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.threeten.bp.Instant;

public class ErrorResultGsonTest extends IndividualResultGsonTest {
    private static final String ERROR_MESSAGE = "An error happened";

    private static final Result FULL = new ErrorResultBase("errorResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(30), ERROR_MESSAGE,
            new ErrorResultThrowable(ERROR_MESSAGE));

    private static final Result NO_THROWABLE = new ErrorResultBase("errorResult", Instant.ofEpochSecond(20),
            Instant.ofEpochSecond(30), ERROR_MESSAGE, null);

    @Test
    public void testErrorResult_Full() {
        Result result = readJsonFile(resultTestComponent.gson(), "results/ErrorResult_Full.json", Result.class);

        assertTrue(result instanceof ErrorResult);

        ErrorResult errorResult = (ErrorResult) result;
        assertEquals(ERROR_MESSAGE, errorResult.getErrorDescription());
        assertNotNull(errorResult.getThrowable());
        assertEquals(ERROR_MESSAGE, errorResult.getThrowable().getMessage());
    }

    @Test
    public void testErrorResult_NoThrowable() {
        testCommon(NO_THROWABLE, "ErrorResult_NoThrowable.json");
    }

    @Test
    public void testSerializationDeserializationIntegration_Full() {
        testSerializationThenDeserialization(FULL);
    }

    @Test
    public void testSerializationDeserializationIntegration_NoThrowable() {
        testSerializationThenDeserialization(NO_THROWABLE);
    }

    private void assertEquals(final String an_error_happened, final String errorDescription) {
    }
}
