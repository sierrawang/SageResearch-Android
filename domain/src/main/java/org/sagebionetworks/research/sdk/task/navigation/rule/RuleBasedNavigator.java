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

package org.sagebionetworks.research.sdk.task.navigation.rule;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.sagebionetworks.research.sdk.result.TaskResult;
import org.sagebionetworks.research.sdk.step.Step;
import org.sagebionetworks.research.sdk.task.Task.Progress;
import org.sagebionetworks.research.sdk.task.navigation.StepNavigator;
import org.sagebionetworks.research.sdk.task.navigation.rule.ConditionalRule.Replacement;
import org.sagebionetworks.research.sdk.task.navigation.rule.NavigationRule.Next.Identifiers;
import org.sagebionetworks.research.sdk.task.navigation.rule.NavigationRule.Skip;

public class RuleBasedNavigator implements StepNavigator {
    @NonNull
    private final ImmutableList<Step> steps;

    @Nullable
    private final ConditionalRule conditionalRule;

    @NonNull
    private final ImmutableMap<String, Step> stepsById;

    public RuleBasedNavigator(@NonNull final ImmutableList<Step> steps,
        @Nullable final ConditionalRule conditionalRule) {
        this.steps = steps;
        this.conditionalRule = conditionalRule;

        Builder<String, Step> mapBuilder = ImmutableMap.builderWithExpectedSize(this.steps.size());
        for (Step step : this.steps) {
            mapBuilder.put(step.getIdentifier(), step);
        }
        this.stepsById = mapBuilder.build();
    }

    @Override
    public Step getNextStep(final Step step, @NonNull TaskResult taskResult) {
        Step nextStep = null;
        Step previousStep = step;
        boolean shouldSkip;
        do {
            shouldSkip = false;
            if (previousStep == null) {
                nextStep = steps.get(0);
            } else {
                String nextStepIdentifier = getNextStepIdentifier(previousStep, taskResult);
                if (nextStepIdentifier != null) {
                    if (Identifiers.Exit.getKey().equals(nextStepIdentifier)) {
                        return null;
                    } else {
                        nextStep = getStep(nextStepIdentifier);
                    }
                }
                if (nextStep == null) {
                    int indexOfNextStep = steps.indexOf(previousStep) + 1;
                    if (indexOfNextStep < steps.size()) {
                        return steps.get(indexOfNextStep);
                    }
                }
            }

            String nextStepIdentifier = conditionalRule.skipToStep(previousStep, taskResult);
            if (Identifiers.NextStep.getKey().equals(nextStepIdentifier)) {
                shouldSkip = true;
            } else {
                nextStep = getStep(nextStepIdentifier);
            }

            if (!shouldSkip && nextStep != null && step instanceof Skip) {
                shouldSkip = ((Skip) step).shouldSkip(taskResult);
            }

            if (shouldSkip) {
                previousStep = nextStep;
            }
        } while (shouldSkip);

        if (conditionalRule instanceof Replacement) {
            nextStep = ((Replacement) conditionalRule).getReplacementStep(nextStep, taskResult);
        }
        return nextStep;
    }

    @Nullable
    @VisibleForTesting
    String getNextStepIdentifier(@NonNull Step step, @NonNull TaskResult taskResult) {
        String nextStepIdentifier;
        if (step instanceof NavigationRule.Next) {
            NavigationRule.Next nextRule = (NavigationRule.Next) step;
            nextStepIdentifier = nextRule.getNextStepIdentifier(taskResult);
        } else {
            nextStepIdentifier = conditionalRule.nextStepIdentifier(step, taskResult);
        }
        return nextStepIdentifier;
    }

    @Override
    public Step getPreviousStep(@NonNull final Step step, @NonNull TaskResult taskResult) {
        return null;
    }

    @NonNull
    @Override
    public Progress getProgress(@NonNull final Step step, @NonNull TaskResult taskResult) {
        return null;
    }

    @Override
    @Nullable
    public Step getStep(@NonNull final String identifier) {
        return stepsById.get(identifier);
    }
}
