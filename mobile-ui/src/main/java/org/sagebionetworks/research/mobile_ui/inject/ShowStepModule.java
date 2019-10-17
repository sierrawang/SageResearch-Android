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

package org.sagebionetworks.research.mobile_ui.inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.sagebionetworks.research.mobile_ui.show_step.view.FormUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowActiveUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowCountdownStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragment;
import org.sagebionetworks.research.presentation.inject.DrawableModule;
import org.sagebionetworks.research.presentation.model.implementations.ActiveUIStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.CountdownStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.FormUIStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.UIStepViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.Multibinds;

// The ShowUIStepViewFragment needs the drawable module so it doesn't make sense to use one of these modules without
// the other.
@Module(includes = DrawableModule.class)
public abstract class ShowStepModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowStepModule.class);

    @MapKey
    public @interface StepViewKey {
        String value();
    }

    @Multibinds
    abstract Map<String, ShowStepFragmentFactory> fragmentFactoryMap();

    public interface ShowStepFragmentFactory {
        @NonNull
        Fragment create(@NonNull StepView stepView);
    }

    @Provides
    public static ShowStepFragmentFactory provideShowStepFragmentFactory(
            Map<String, ShowStepFragmentFactory> showStepFragmentFactoryMap) {
        return (@NonNull StepView stepView) -> {
            if (showStepFragmentFactoryMap.containsKey(stepView.getType())) {
                Fragment fragment = showStepFragmentFactoryMap.get(stepView.getType()).create(stepView);
                LOGGER.debug("Created fragment with class: {} from stepView: {}",
                        fragment.getClass().getCanonicalName(), stepView);
                if (fragment !=null ){
                    return fragment;
                }
            }

            // If we don't have a factory we default to the most general ShowStepFragment.
            return ShowUIStepFragment.newInstance(stepView);
        };
    }

    @Provides
    @IntoMap
    @StepViewKey(ActiveUIStepViewBase.TYPE)
    static ShowStepFragmentFactory provideShowActiveUIStepFragmentFactory() {
        return ShowActiveUIStepFragment::newInstance;
    }

    @Provides
    @IntoMap
    @StepViewKey(UIStepViewBase.TYPE)
    static ShowStepFragmentFactory provideShowUIStepFragmentFactory() {
        return ShowUIStepFragment::newInstance;
    }

    @Provides
    @IntoMap
    @StepViewKey(FormUIStepViewBase.TYPE)
    static ShowStepFragmentFactory provideFormUIStepFragmentFactory() {
        return FormUIStepFragment.Companion::newInstance;
    }

    @Provides
    @IntoMap
    @StepViewKey(CountdownStepViewBase.TYPE)
    static ShowStepFragmentFactory provideShowCountdownStepFragmentFactory() {
        return ShowCountdownStepFragment::newInstance;
    }
}
