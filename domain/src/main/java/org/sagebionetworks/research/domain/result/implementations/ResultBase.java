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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;

import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.interfaces.ObjectHelper;
import org.sagebionetworks.research.domain.result.ResultType;
import org.sagebionetworks.research.domain.result.data.ResultData;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.threeten.bp.Instant;


/**
 * The base class concrete implementation for all result objects. A wrapper around ResultData which adds behavior and
 * can be subclassed.
 */
public class ResultBase extends ObjectHelper implements Result {
    @ResultType
    public static final String TYPE_KEY = ResultType.BASE;

    // Subclasses shouldn't hide this field as doing so will result in a gson error.
    @NonNull
    protected final ResultData data;

    // This field is present to make gson serialize it.
    protected final String type;

    /**
     * Constructor for subclasses to use to allow them to have the correct type. If the given type is null the type
     * for this result is assumed to be ResultType.BASE.
     *
     * @param identifier
     *         The identifier of the result type.
     * @param startTime
     *         The start Instant of this result.
     * @param endTime
     *         The end Instant of this result.
     */
    public ResultBase(@NonNull final String identifier,
            @NonNull final Instant startTime, @NonNull final Instant endTime) {
        super();
        this.type = this.getType();
        this.data = ResultData.create(identifier, startTime, endTime);
    }

    @Nullable
    @Override
    public Instant getEndTime() {
        return this.data.getEndTime();
    }

    @Override
    @NonNull
    public String getIdentifier() {
        return this.data.getIdentifier();
    }

    @Override
    @NonNull
    public Instant getStartTime() {
        return this.data.getStartTime();
    }

    @Override
    @NonNull
    @ResultType
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        ResultBase resultBase = (ResultBase) o;
        return Objects.equal(this.data, resultBase.data);
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.data);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("ResultData", data);
    }
}
