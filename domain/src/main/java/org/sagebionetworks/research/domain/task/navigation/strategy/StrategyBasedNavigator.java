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

package org.sagebionetworks.research.domain.task.navigation.strategy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.domain.task.navigation.TreeNavigator;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.BackStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.NextStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.SkipStepStrategy;

import java.util.List;

public class StrategyBasedNavigator implements StepNavigator {
    // The tree navigator that backs up this StrategyBasedNavigator whenever the various navigation rules aren't
    // applicable.
    @NonNull
    private final TreeNavigator treeNavigator;

    /**
     * Constructs a new StrategyBasedNavigator from the given list of steps, and the given list of progress markers.
     * @param steps The list of steps to create this StepBasedNavigator from.
     * @param progressMarkers The list of progress markers to create this StepBasedNavigator from.
     */
    public StrategyBasedNavigator(@NonNull final List<Step> steps, @Nullable List<String> progressMarkers) {
        this.treeNavigator = new TreeNavigator(steps, progressMarkers);
    }

    @Override
    public Step getNextStep(final Step step, @NonNull TaskResult taskResult) {
        Step nextStep = null;
        // First we try to get the next step from the step by casting it to a NextStepStrategy.
        if (step instanceof NextStepStrategy) {
            String nextStepId = ((NextStepStrategy)step).getNextStepIdentifier(taskResult);
            if (nextStepId != null) {
                nextStep = this.getStep(nextStepId);
            }
        }

        // If we don't get a valid step from casting to a NextStepStrategy we default to using the tree navigator to
        // get the next step.
        if (nextStep == null) {
            nextStep = treeNavigator.getNextStep(step, taskResult);
        }

        if (nextStep != null) {
            // As long as the next step we have found shouldn't be skipped we return it.
            if (!(nextStep instanceof SkipStepStrategy) ||
                    !((SkipStepStrategy)nextStep).shouldSkip(taskResult)) {
                return nextStep;
            }

            // If we should skip the next step we found, we recurse on the next step to get the one after that.
            return getNextStep(nextStep, taskResult);
        }

        // If the tree navigator returns null we also return null.
        return null;
    }

    @Override
    public Step getPreviousStep(@NonNull final Step step, @NonNull TaskResult taskResult) {
        // First we make sure that the given step allows backward navigation.
        if (step instanceof BackStepStrategy && !((BackStepStrategy)step).isBackAllowed(taskResult)) {
           return null;
        }

        // If backward navigation is allowed we check the result.
        Step previousStep = null;
        List<Result> stepHistory = taskResult.getStepHistory();
        int idx = stepHistory.indexOf(taskResult.getResult(step.getIdentifier()));
        if (idx > 0) {
            String previousStepId = stepHistory.get(idx - 1).getIdentifier();
            previousStep = this.getStep(previousStepId);
        }

        // If the task result doesn't give us a previous step to go back to we default to using the tree navigator
        // to get a previous step.
        if (previousStep == null) {
            previousStep = this.treeNavigator.getPreviousStep(step, taskResult);
        }

        return previousStep;
    }

    @Nullable
    @Override
    public TaskProgress getProgress(@NonNull final Step step, @NonNull TaskResult taskResult) {
        return this.treeNavigator.getProgress(step, taskResult);
    }

    @Override
    @Nullable
    public Step getStep(@NonNull final String identifier) {
        return this.treeNavigator.getStep(identifier);
    }

    @NotNull
    @Override
    public List<Step> getSteps() {
        return this.treeNavigator.getSteps();
    }

    public static class Factory implements StepNavigatorFactory {
        @Override
        public StepNavigator create(final List<Step> steps, final List<String> progressMarkers) {
            return new StrategyBasedNavigator(steps, progressMarkers);
        }
    }
}
