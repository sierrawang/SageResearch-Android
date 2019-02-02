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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.TransformerStep;

import java.lang.reflect.Type;
import java.util.HashSet;

public class TransformerStepBase extends StepBase implements TransformerStep {
    public static final String TYPE_KEY = StepType.TRANSFORM;

    @NonNull
    @SerializedName("resourceTransformer")
    private final String resourceName;

    public static JsonDeserializer<TransformerStepBase> getJsonDeserializer() {
        return new JsonDeserializer<TransformerStepBase>() {
            @Override
            public TransformerStepBase deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    if (json.isJsonObject()) {
                        JsonObject object = json.getAsJsonObject();
                        String identifier = object.get("identifier").getAsString();
                        JsonElement resourceTransformer = object.get("resourceTransformer").getAsJsonObject();
                        String resourceName = resourceTransformer.getAsJsonObject().get("resourceName").getAsString();
                        return new TransformerStepBase(identifier, resourceName);
                    }
                } catch (ClassCastException e) {
                    throw new JsonParseException("Transformer step contained an incorrect field type");
                } catch (IllegalStateException e) {
                    throw new JsonParseException("Transformer steps shouldn't contain any Json arrays");
                }

                throw new JsonParseException("Unknown form for transformer step.");
            }
        };
    }

    public TransformerStepBase(@NonNull final String identifier, @NonNull final String resourceName) {
        // A transformer step has no async actions, however the step it transforms into still might.
        super(identifier, new HashSet<>());
        this.resourceName = resourceName;
    }

    @NonNull
    @Override
    public TransformerStep copyWithIdentifier(@NonNull final String identifier) {
        throw new UnsupportedOperationException("Transformer steps cannot be copied");
    }

    @NonNull
    @Override
    public String getResourceName() {
        return this.resourceName;
    }

    @NonNull
    @Override
    public String getType() {
        return TYPE_KEY;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        TransformerStepBase transformerStep = (TransformerStepBase) o;
        return Objects.equal(this.getIdentifier(), transformerStep.getIdentifier()) &&
                Objects.equal(this.getResourceName(), transformerStep.getResourceName());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.resourceName);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", this.getIdentifier())
                .add("resourceName", this.getResourceName());
    }
}
