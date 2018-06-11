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

package org.sagebionetworks.research.domain.task.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import org.jetbrains.annotations.NotNull;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The OrderedStepNavigator moves through a linear series of steps.
 * <p>
 * Any simple sequential task, such as a survey or an active task, can be presented by an OrderedStepNavigator.
 */
public class OrderedStepNavigator implements StepNavigator {

    public static final class Factory implements StepNavigatorFactory {

        @Override
        public StepNavigator create(List<Step> steps, List<String> progressMarkers) {
            return new OrderedStepNavigator(steps);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(
            OrderedStepNavigator.class);

    private final ImmutableList<Step> steps;

    private final ImmutableMap<String, Step> stepsById;

    OrderedStepNavigator(List<? extends Step> steps) {
        this.steps = ImmutableList.copyOf(steps);

        Builder<String, Step> mapBuilder = ImmutableMap.builderWithExpectedSize(this.steps.size());
        for (Step step : this.steps) {
            mapBuilder.put(step.getIdentifier(), step);
        }
        this.stepsById = mapBuilder.build();
    }

    @Override
    @Nullable
    public Step getStep(@NonNull final String identifier) {
        return stepsById.get(identifier);
    }

    @Nullable
    @Override
    public Step getNextStep(@Nullable Step step, @NonNull TaskResult taskResult) {
        // default to the first step
        int nextStepIndex = 0;

        if (step != null) {
            int stepIndex = steps.indexOf(step);
            if (stepIndex < 0) {
                LOGGER.warn("Unable to locate step: " + step + ", returning null for next step");
                return null;
            }
            nextStepIndex = stepIndex + 1;
        }

        if (nextStepIndex >= steps.size()) {
            return null;
        }

        return steps.get(nextStepIndex);
    }

    @Nullable
    @Override
    public Step getPreviousStep(@NonNull Step step, @NonNull TaskResult taskResult) {
        checkNotNull(step);
        //TODO: ask Shannon how stepHistory works. does it pop off like backstack? does moving
        // backwards append to step history?

        int stepIndex = steps.indexOf(step);

        if (stepIndex < 0) {
            LOGGER.warn("Unable to locate step: " + step + ", returning null for previous step");
            return null;
        } else if (stepIndex == 0) {
            return null;
        }

        return steps.get(stepIndex - 1);
    }

    @NonNull
    @Override
    public TaskProgress getProgress(@NonNull final Step step, @NonNull TaskResult taskResult) {
        int stepNumber = steps.indexOf(step) + 1;
        int totalSteps = steps.size();
        return new TaskProgress(stepNumber, totalSteps, false);
    }

    @NotNull
    @Override
    public List<Step> getSteps() {
        return steps;
    }
}
