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

package org.sagebionetworks.research.sdk.inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.inject.Singleton;
import org.sagebionetworks.research.sdk.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.sdk.step.InstructionStep;
import org.sagebionetworks.research.sdk.step.Step;

@Module
public class GsonModule {
    @Provides
    @Singleton
    static Gson provideGson(Map<Class, JsonDeserializer> jsonDeserializerMap,
        Set<RuntimeTypeAdapterFactory> runtimeTypeAdapterFactories) {
        GsonBuilder builder = new GsonBuilder();
        for (Map.Entry<Class, JsonDeserializer> entry : jsonDeserializerMap.entrySet()) {
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        for (RuntimeTypeAdapterFactory runtimeTypeAdapterFactory : runtimeTypeAdapterFactories) {
            builder.registerTypeAdapterFactory(runtimeTypeAdapterFactory);
        }

        return builder.create();
    }

    @MapKey
    public @interface StepClassKey {
        Class<? extends Step> value();
    }

    @Provides
    @IntoMap
    @StepClassKey(InstructionStep.class)
    static String provideInstructionStepType() {
        return InstructionStep.TYPE_KEY;
    }

    @IntoSet
    @Provides
    static RuntimeTypeAdapterFactory<Step> provideType(Map<Class<? extends Step>, String> stepClasses) {
        RuntimeTypeAdapterFactory<Step> stepAdapterFactory = RuntimeTypeAdapterFactory.of(Step.class, Step.KEY_TYPE);

        for (Entry<Class<? extends Step>, String> stepClassEntry : stepClasses.entrySet()) {
            stepAdapterFactory.registerSubtype(stepClassEntry.getKey(), stepClassEntry.getValue());
        }
        return stepAdapterFactory;
    }
}
