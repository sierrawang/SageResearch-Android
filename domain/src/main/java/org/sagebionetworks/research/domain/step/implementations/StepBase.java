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

import static com.google.common.base.Preconditions.checkNotNull;

import static java.util.Optional.of;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.interfaces.ObjectHelper;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.Step;

import java.util.Collections;
import java.util.Set;


public abstract class StepBase extends ObjectHelper implements Step {
    public final String TYPE_KEY = StepType.BASE;

    @NonNull
    private final String identifier;

    @NonNull
    private final ImmutableSet<AsyncActionConfiguration> asyncActions;

    public StepBase(@NonNull String identifier, @Nullable Set<AsyncActionConfiguration> asyncActions) {
        super();
        asyncActions = asyncActions != null ? asyncActions : Collections.emptySet();
        this.asyncActions = ImmutableSet.copyOf(asyncActions);
        this.identifier = checkNotNull(identifier);
    }

    @Override
    @NonNull
    public ImmutableSet<AsyncActionConfiguration> getAsyncActions() {
        return this.asyncActions;
    }

    @Override
    @NonNull
    public String getIdentifier() {
        return this.identifier;
    }

    @NonNull
    @Override
    public String getType() {
        // Should be overriden by subclasses.
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        StepBase stepBase = (StepBase) o;
        return Objects.equal(this.getIdentifier(), stepBase.getIdentifier());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.identifier);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("identifier", identifier);
    }
}
