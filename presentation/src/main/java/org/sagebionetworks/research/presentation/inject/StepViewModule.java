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

package org.sagebionetworks.research.presentation.inject;

import android.support.annotation.Nullable;

import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;
import org.sagebionetworks.research.presentation.model.implementations.ActiveUIStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.CountdownStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.FormUIStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.UIStepViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;

import java.util.Map;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.Multibinds;

@Module(includes = DrawableModule.class)
public abstract class StepViewModule {
    // We use the return value of the getType() method on the step as the key which maps to a function that turns
    // the step into a step view.
    @MapKey
    public @interface StepTypeKey {
        String value();
    }

    public interface StepViewFactory {
        @Nullable
        StepView apply(Step step);
    }

    public interface InternalStepViewFactory {
        @Nullable
        StepView apply(Step step, DrawableMapper mapper);
    }

    @Multibinds
    abstract Map<String, InternalStepViewFactory> stepToFactoryMap();

    @Provides
    @IntoMap
    @StepTypeKey(StepType.ACTIVE)
    static InternalStepViewFactory provideActiveUIStepFactory() {
        return ActiveUIStepViewBase::fromActiveUIStep;
    }

    @Provides
    @IntoMap
    @StepTypeKey(StepType.FORM)
    static InternalStepViewFactory provideFormUIStepFactory() {
        return FormUIStepViewBase::fromFormUIStep;
    }

    @Provides
    @IntoMap
    @StepTypeKey(StepType.UI)
    static InternalStepViewFactory provideUIStepFactory() {
        return UIStepViewBase::fromUIStep;
    }

    @Provides
    @IntoMap
    @StepTypeKey(StepType.COUNTDOWN)
    static InternalStepViewFactory provideCountdownStepFactory() {
        return CountdownStepViewBase::fromCountdownStep;
    }

    @Provides
    static StepViewFactory provideStepViewFactory(
            final Map<String, InternalStepViewFactory> stepToFactoryMap,
            final DrawableMapper drawableMapper) {
        return (final Step step) ->
        {
            String type = step.getType();
            if (stepToFactoryMap.containsKey(type)) {
                return stepToFactoryMap.get(type).apply(step, drawableMapper);
            } else {
                return UIStepViewBase.fromUIStep(step, drawableMapper);
            }
        };
    }
}
