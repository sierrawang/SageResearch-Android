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

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.step.ActiveUIStepBase;
import org.sagebionetworks.research.domain.step.FormUIStepBase;
import org.sagebionetworks.research.domain.step.SectionStep;
import org.sagebionetworks.research.domain.step.SectionStepBase;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.TransformerStep;
import org.sagebionetworks.research.domain.step.TransformerStepBase;
import org.sagebionetworks.research.domain.step.UIStepBase;
import org.sagebionetworks.research.domain.step.ui.ActiveUIStep;
import org.sagebionetworks.research.domain.step.ui.FormUIStep;
import org.sagebionetworks.research.domain.step.ui.UIStep;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughDeserializer;

@Module(includes = {GsonModule.class})
public class StepModule {
    @MapKey
    public @interface StepClassKey {
        Class<? extends Step> value();
    }

    // region Type Keys
    @Provides
    @IntoMap
    @StepClassKey(ActiveUIStep.class)
    static String provideActiveUIStepTypeKey() {
        return ActiveUIStepBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @StepClassKey(FormUIStep.class)
    static String provideFormUIStepTypeKey() {
        return FormUIStepBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @StepClassKey(SectionStep.class)
    static String provideSectionStepTypeKey() {
        return SectionStepBase.TYPE_KEY;
    }


    @Provides
    @IntoMap
    @StepClassKey(UIStep.class)
    static String provideUIStepTypeKey() {
        return UIStepBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @StepClassKey(TransformerStep.class)
    static String provideTransformerStepTypeKey() {
        return TransformerStepBase.TYPE_KEY;
    }
    // endregion


    // region Step Deserializers
    @Provides
    @IntoMap
    @ClassKey(ActiveUIStep.class)
    static JsonDeserializer<?> provideActiveUIStepDeserializer() {
        return createPassThroughDeserializer(ActiveUIStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(FormUIStep.class)
    static JsonDeserializer<?> providedFormUIStepDeserializer() {
        return createPassThroughDeserializer(FormUIStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(SectionStep.class)
    static JsonDeserializer<?> provideSectionStepDeserializer() {
       return createPassThroughDeserializer(SectionStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(UIStep.class)
    static JsonDeserializer<?> providedUIStepDeserializer() {
        return createPassThroughDeserializer(UIStepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(TransformerStep.class)
    static JsonDeserializer<?> provideTransformerStepDeserializer() {
        return TransformerStepBase.getJsonDeserializer();
    }
    // endregion

    /**
     * @return GSON runtime type adapter factory for polymorphic deserialization of Step classes
     */
    @Provides
    @IntoSet
    static RuntimeTypeAdapterFactory provideType(Map<Class<? extends Step>, String> stepClassKeys) {
        RuntimeTypeAdapterFactory<Step> stepAdapterFactory = RuntimeTypeAdapterFactory.of(Step.class, Step.KEY_TYPE);

        for (Entry<Class<? extends Step>, String> stepClassEntry : stepClassKeys.entrySet()) {
            stepAdapterFactory.registerSubtype(stepClassEntry.getKey(), stepClassEntry.getValue());
        }

        return stepAdapterFactory.registerDefaultType(UIStep.class);
    }
}
