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

package org.sagebionetworks.research.domain.step.implementations;

import android.support.annotation.NonNull;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SectionStepBase extends StepBase implements SectionStep {
    public static final String TYPE_KEY = StepType.SECTION;
    private static final Logger LOGGER = LoggerFactory.getLogger(SectionStepBase.class);

    @NonNull
    private final ImmutableList<Step> steps;

    public SectionStepBase(@NonNull final String identifier, @NonNull final List<Step> steps) {
        super(identifier);
        this.steps = ImmutableList.copyOf(steps);
    }

    @NonNull
    @Override
    public SectionStepBase copyWithIdentifier(String identifier) {
        SectionStepBase result = new SectionStepBase(identifier, steps);
        // If the user forgets to override copy with identifier, the type of the step will change when it goes through
        // the resource transformer. This is a really confusing bug so this code is present to make it clearer why
        // this is happening.
        if (result.getClass() != this.getClass()) {
            LOGGER.warn("Result of copy with identifier has different type than original input, did you"
                    + "forget to override CopyWithIdentifier");
        }

        return result;
    }

    @NonNull
    @Override
    public ImmutableList<Step> getSteps() {
        return this.steps;
    }

    @NonNull
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        SectionStepBase sectionStep = (SectionStepBase) o;
        return super.equalsHelper(o) &&
                Objects.equal(this.getSteps(), sectionStep.getSteps());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.steps);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("steps", this.getSteps());
    }
}
