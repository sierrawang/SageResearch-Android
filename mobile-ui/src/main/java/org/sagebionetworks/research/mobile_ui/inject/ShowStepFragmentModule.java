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

import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.step.implementations.CompletionStepBase;
import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowActiveUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowCompletionStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowFormUIStepFragment;
import org.sagebionetworks.research.presentation.inject.DrawableModule;
import org.sagebionetworks.research.presentation.model.implementations.ActiveUIStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.CompletionStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.FormUIStepViewBase;
import org.sagebionetworks.research.presentation.model.implementations.UIStepViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;

import java.util.Map;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

// The ShowUIStepViewFragment needs the drawable module so it doesn't make sense to use one of these modules without
// the other.
@Module(includes = DrawableModule.class)
public class ShowStepFragmentModule {
    @MapKey
    public @interface StepViewKey {
        String value();
    }

    public interface ShowStepFragmentFactory {
        @NonNull
        ShowStepFragmentBase create(@NonNull StepView stepView,
                                    @NonNull PerformTaskFragment performTaskFragment);
    }

    @Provides
    public static ShowStepFragmentFactory provideShowStepFragmentFactory(
            Map<String, ShowStepFragmentFactory> showStepFragmentFactoryMap) {
        return (@NonNull StepView stepView, @NonNull PerformTaskFragment performTaskFragment) -> {
            if (showStepFragmentFactoryMap.containsKey(stepView.getType())) {
                return showStepFragmentFactoryMap.get(stepView.getType()).create(stepView,
                        performTaskFragment);
            }

            // If we don't have a factory we default to the most general ShowStepFragment.
            return ShowUIStepFragment.newInstance(stepView, performTaskFragment);
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
    @StepViewKey(CompletionStepViewBase.TYPE)
    static ShowStepFragmentFactory provideShowCompletionStepFragmentFactory() {
        return ShowCompletionStepFragment::newInstance;
    }

    @Provides
    @IntoMap
    @StepViewKey(FormUIStepViewBase.TYPE)
    static ShowStepFragmentFactory provideShowFormUIStepFragmentFactory() {
        return ShowFormUIStepFragment::newInstance;
    }
}
