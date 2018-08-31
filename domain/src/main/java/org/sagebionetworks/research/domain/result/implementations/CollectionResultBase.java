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

package org.sagebionetworks.research.domain.result.implementations;

import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.result.ResultType;
import org.sagebionetworks.research.domain.result.data.CollectionResultData;
import org.sagebionetworks.research.domain.result.interfaces.CollectionResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;

/**
 * The concrete implementation of a result made from a group of other results. This is a wrapper around
 * CollectionResultData which adds behavior and can be subclassed.
 */
public class CollectionResultBase extends ResultBase implements CollectionResult {
    @ResultType
    public static final String TYPE_KEY = ResultType.COLLECTION;

    // Subclasses should not hide this field as doing so will result in a gson error.
    private final CollectionResultData collectionResultData;

    public CollectionResultBase(@NonNull final String identifier, @NonNull final Instant startTime,
            @NonNull final Instant endTime, @NonNull final List<Result> inputResults) {
        super(identifier, startTime, endTime);
        this.collectionResultData = CollectionResultData.create(inputResults);
    }

    @Override
    @NonNull
    public CollectionResult appendInputResult(@NonNull Result inputResult) {
        List<Result> inputResults = new ArrayList<>(this.getInputResults());
        inputResults.add(inputResult);
        return new CollectionResultBase(this.getIdentifier(), this.getStartTime(),
                this.getEndTime(), inputResults);
    }

    @NonNull
    @Override
    public ImmutableList<Result> getInputResults() {
        return this.collectionResultData.getInputResults();
    }

    @Override
    @NonNull
    public Instant getEndTime() {
        return super.getEndTime();
    }

    @NonNull
    @ResultType
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        CollectionResultBase collectionResult = (CollectionResultBase) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.collectionResultData, collectionResult.collectionResultData);
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.collectionResultData);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("TaskResultData", this.collectionResultData);
    }
}
