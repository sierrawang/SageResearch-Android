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

package org.sagebionetworks.research.presentation.show_step.show_step_view_model_factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.sagebionetworks.research.presentation.model.BaseStepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowStepViewModel;

import java.util.Map;

import javax.inject.Inject;

public class AbstractShowStepViewModelFactory {
    private final Map<String, ShowStepViewModelFactory<?, ? extends StepView>> t;

    @Inject
    public AbstractShowStepViewModelFactory(
            Map<String, ShowStepViewModelFactory<?, ? extends StepView>> t) {
        this.t = t;
    }

    public ViewModelProvider.Factory create(PerformTaskViewModel performTaskViewModel, final StepView stepView) {
        return new ViewModelProvider.Factory() {

            @NonNull
            @Override
            @SuppressWarnings(value = "unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                ShowStepViewModelFactory af = getFactory(stepView);

                if (modelClass.isAssignableFrom(af.getViewModelClass())) {
                    // noinspection unchecked
                    return (T) af.create(performTaskViewModel, stepView);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        };
    }

    public Class<? extends ShowStepViewModel> getViewModelClass(final StepView stepView) {
        return getFactory(stepView).getViewModelClass();
    }

    private ShowStepViewModelFactory<?, ? extends StepView> getFactory(final StepView stepView) {
        ShowStepViewModelFactory<?, ? extends StepView> factory = t.get(stepView.getType());
        if (factory == null) {
            factory = t.get(BaseStepView.TYPE);
        }

        return factory;
    }
}
