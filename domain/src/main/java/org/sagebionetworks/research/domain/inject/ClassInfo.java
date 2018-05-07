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

package org.sagebionetworks.research.domain.inject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonDeserializer;

public class ClassInfo<T> {
    @NonNull
    private final Class<T> implementationClass;
    @Nullable
    private final String typeKey;
    @Nullable
    private final JsonDeserializer<? extends T> jsonDeserializer;

    public ClassInfo(@NonNull final Class<T> implementationClass,
            @Nullable final String typeKey, @Nullable final JsonDeserializer<? extends T> jsonDeserializer) {
        this.implementationClass = implementationClass;
        this.typeKey = typeKey;
        this.jsonDeserializer = jsonDeserializer;
    }

    public boolean hasCustomJsonDeserializer() {
        return this.jsonDeserializer != null;
    }

    @NonNull
    public Class<T> getImplementationClass() {
        return implementationClass;
    }

    @Nullable
    public String getTypeKey() {
        return typeKey;
    }

    @Nullable
    public JsonDeserializer<? extends T> getJsonDeserializer() {
        return jsonDeserializer;
    }
}
