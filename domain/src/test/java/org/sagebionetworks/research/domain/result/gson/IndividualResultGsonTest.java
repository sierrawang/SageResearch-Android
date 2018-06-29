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

import static junit.framework.Assert.assertNotNull;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.sagebionetworks.research.domain.JsonAssetUtil;
import org.sagebionetworks.research.domain.result.DaggerResultTestComponent;
import org.sagebionetworks.research.domain.result.ResultTestComponent;
import org.sagebionetworks.research.domain.result.interfaces.ErrorResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;

public class IndividualResultGsonTest {
    protected ResultTestComponent resultTestComponent;

    @Before
    public void setup() {
        this.resultTestComponent = DaggerResultTestComponent.builder().build();
    }

    protected void testCommon(Result expected, String filename) {
        JsonAssetUtil
                .assertJsonFileEqualRef(expected, resultTestComponent.gson(), "results/" + filename, Result.class);
    }

    protected void testSerializationThenDeserialization(Result result) {
        String serialized = this.resultTestComponent.gson().toJson(result);
        assertNotNull(serialized);
        Result deserialized = this.resultTestComponent.gson().fromJson(serialized, Result.class);
        assertNotNull(deserialized);
        if (deserialized instanceof ErrorResult) {
            String message = ((ErrorResult) result).getErrorDescription();
            ErrorResult errorResult = (ErrorResult) result;
            assertEquals(message, errorResult.getErrorDescription());
            if (errorResult.getThrowable() != null) {
                assertEquals(message, errorResult.getThrowable().getMessage());
            }
        } else {
            assertEquals(result, deserialized);
        }
    }
}
