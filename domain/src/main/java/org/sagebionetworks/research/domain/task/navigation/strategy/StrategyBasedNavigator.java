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
import org.sagebionetworks.research.domain.result.interfaces.NavigationResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.navigation.NavDirection;
import org.sagebionetworks.research.domain.task.navigation.StepAndNavDirection;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.domain.task.navigation.TreeNavigator;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.BackStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.NextStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.SkipStepStrategy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class StrategyBasedNavigator implements StepNavigator {
    public static class Factory implements StepNavigatorFactory {
        @Override
        public StepNavigator create(final Task task, final List<String> progressMarkers) {
            return new StrategyBasedNavigator(task, progressMarkers);
        }
    }

    // The tree navigator that backs up this StrategyBasedNavigator whenever the various navigation rules aren't
    // applicable.
    @NonNull
    private final TreeNavigator treeNavigator;
    @NonNull
    private final Task task;

    /**
     * Constructs a new StrategyBasedNavigator from the given list of steps, and the given list of progress markers.
     *
     * @param task
     *         The task to create this StepBasedNavigator from.
     * @param progressMarkers
     *         The list of progress markers to create this StepBasedNavigator from.
     */
    public StrategyBasedNavigator(@NonNull final Task task, @Nullable List<String> progressMarkers) {
        this.task = task;
        this.treeNavigator = new TreeNavigator(task.getSteps(), progressMarkers);
    }

    /**
     * Only supported return values are NavDirection values
     * @param fromStep step to be considered where the user is currently
     * @param toStep step to be considered where the user is going to go next
     * @return the navigation direction moving from fromStep to toStep
     */
    private @NavDirection int getNextStepDirection(@Nullable final Step fromStep,
            @Nullable final Step toStep, @NotNull final TaskResult taskResult) {

        // The StrategyBasedNavigator's next step can potentially be a previously visited step.
        // In that case, we want to identify it, and provide a more logical transition to the user with SHIFT_RIGHT.
        if (fromStep != null && toStep != null) {
            // This is a simple index check of step list order, more complex logic may be needed,
            // If so, this can be adjusted, or getNextStepDirection can be overridden
            int fromIndex = indexOfStep(fromStep);
            int toIndex = indexOfStep(toStep);

            if (toIndex < fromIndex) {
                return NavDirection.SHIFT_RIGHT;
            }
        }

        return NavDirection.SHIFT_LEFT;
    }

    @Override
    @Nullable
    public Step getStep(@NonNull final String identifier) {
        return this.treeNavigator.getStep(identifier);
    }

    @Override
    public @Nonnull StepAndNavDirection getNextStep(final Step step, @NonNull TaskResult taskResult) {
        Step nextStep = null;

        // First we try to get the next step from the result by casting it to a NavigationResult
        String skipToIdentifier = getSkipToIdentifierFromNavigationResult(step, taskResult);
        if (skipToIdentifier != null) {
            nextStep = treeNavigator.getStep(skipToIdentifier);
        }

        // If we don't get a valid step from casting the result to a NavigationResult,
        // let's try to get the next step from the step by casting it to a NextStepStrategy.
        if (nextStep == null && step instanceof NextStepStrategy) {
            String nextStepId = ((NextStepStrategy)step).getNextStepIdentifier(taskResult);
            if (nextStepId != null) {
                nextStep = this.getStep(nextStepId);
            }
        }

        // If we didn't get a valid step from the previous checks, we default to using the tree navigator to
        // get the next step.
        if (nextStep == null) {
            nextStep = treeNavigator.getNextStep(step, taskResult).getStep();
        }

        if (nextStep != null) {
            nextStep = StrategyBasedNavigator.resolveSection(nextStep);

            // As long as the next step we have found shouldn't be skipped we return it.
            if (!(nextStep instanceof SkipStepStrategy) ||
                    !((SkipStepStrategy) nextStep).shouldSkip(taskResult)) {
                @NavDirection int navDirection = getNextStepDirection(step, nextStep, taskResult);
                return new StepAndNavDirection(nextStep, navDirection);
            }

            // If we should skip the next step we found, we recurse on the next step to get the one after that.
            return this.getNextStep(nextStep, taskResult);
        }

        // If the tree navigator returns null we also return null.
        return new StepAndNavDirection(null, NavDirection.SHIFT_LEFT);
    }

    @Override
    public Step getPreviousStep(@NonNull final Step step, @NonNull TaskResult taskResult) {
        Step result = this.getPreviousStepHelper(step, taskResult);
        // In the event that the helper returns a SectionStep we traverse through the section's children to get a
        // non-section step.
        return StrategyBasedNavigator.resolveSection(result);
    }

    private Step getPreviousStepHelper(@NonNull final Step step, @NonNull TaskResult taskResult) {
        // First we make sure that the given step allows backward navigation.
        if (step instanceof BackStepStrategy && !((BackStepStrategy) step).isBackAllowed(taskResult)) {
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

    @NotNull
    @Override
    public List<Step> getSteps() {
        return this.treeNavigator.getSteps();
    }

    protected static Step resolveSection(@Nullable Step step) {
        while (step instanceof SectionStep) {
            step = ((SectionStep) step).getSteps().get(0);
        }

        return step;
    }

    /**
     * Performs a section resolved dive into indexing the steps to use for navigation direction
     * @param step to find the index of
     * @return the index of the step when the task steps are flattened
     */
    private int indexOfStep(Step step) {
        List<Step> flattenedSteps = new ArrayList<>();
        flattenTaskSteps(task.getSteps(), flattenedSteps);
        return flattenedSteps.indexOf(step);
    }

    /**
     * Builds a flattened list of steps from the task by resolving sections
     * @param outStepList the pass by reference step list to keep adding steps to
     */
    private void flattenTaskSteps(@Nullable List<Step> inStepList, @Nonnull List<Step> outStepList) {
        if (inStepList == null || inStepList.isEmpty()) {
            return;
        }
        for (Step step : inStepList) {
            if (step instanceof SectionStep) {
                SectionStep sectionStep = (SectionStep)step;
                outStepList.add(sectionStep);
                flattenTaskSteps(sectionStep.getSteps(), outStepList);
            } else {
                outStepList.add(step);
            }
        }
    }

    /**
     * @param step current step that would contain the possible NavigationResult
     * @param taskResult current task result for the task
     * @return an identifier that the navigator should skip to based on a specific NavigationResult scenario,
     *         null otherwise.
     */
    private @Nullable String getSkipToIdentifierFromNavigationResult(Step step, TaskResult taskResult) {
        if (step != null) {
            Result result = taskResult.getResult(step);
            if (result instanceof NavigationResult) {
                return ((NavigationResult)result).getSkipToIdentifier();
            }
        }
        return null;
    }
}
