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

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughDeserializer;

import com.google.gson.JsonDeserializer;

import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.step.ui.action.implementations.ActionBase;
import org.sagebionetworks.research.domain.step.ui.action.implementations.ReminderActionBase;
import org.sagebionetworks.research.domain.step.ui.action.implementations.SkipToStepActionBase;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.ReminderAction;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.SkipToStepAction;

import java.util.Map;
import java.util.Map.Entry;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

@Module(includes = GsonModule.class)
public class ActionModule {
    @MapKey
    public @interface ActionKey {
        Class<? extends Action> value();
    }

    @Provides
    @IntoMap
    @ActionKey(Action.class)
    static String provideActionTypeKey() {
        return ActionBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ActionKey(ReminderAction.class)
    static String provideReminderActionTypeKey() {
        return ReminderActionBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ActionKey(SkipToStepAction.class)
    static String proivdeSkipToStepActionTypeKey() {
        return SkipToStepActionBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ClassKey(Action.class)
    static JsonDeserializer provideActionDeserializer() {
        return createPassThroughDeserializer(ActionBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(ReminderAction.class)
    static JsonDeserializer provideReminderActionDeserializer() {
        return createPassThroughDeserializer(ReminderActionBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(SkipToStepAction.class)
    static JsonDeserializer provideSkipToStepActionDeserializer() {
        return createPassThroughDeserializer(SkipToStepActionBase.class);
    }

    @Provides
    @IntoSet
    static RuntimeTypeAdapterFactory provideActionTypeAdapterFactory(Map<Class<? extends Action>, String> typeMap) {
        RuntimeTypeAdapterFactory factory = RuntimeTypeAdapterFactory.of(Action.class);
        for (Entry<Class<? extends Action>, String> entry : typeMap.entrySet()) {
            factory.registerSubtype(entry.getKey(), entry.getValue());
        }

        factory.registerDefaultType(Action.class);
        return factory;
    }
}
