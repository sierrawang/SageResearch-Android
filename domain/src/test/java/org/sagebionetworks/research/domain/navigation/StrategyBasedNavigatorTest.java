package org.sagebionetworks.research.domain.navigation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.BackStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.NextStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.SkipStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StrategyBasedNavigator;

import java.util.ArrayList;
import java.util.List;

public class StrategyBasedNavigatorTest extends IndividualNavigatorTest {
    private static final String SKIP_RESULT_IDENTIFIER = "skip";

    private static final String TEST_STEP_TYPE = "test step";

    private static final List<Step> STRATEGY_TEST_STEPS;

    private static final StrategyBasedNavigator STRATEGY_TEST_NAVIGATOR;

    public StrategyBasedNavigatorTest() {
        super(new StrategyBasedNavigator(TEST_STEPS, TEST_PROGRESS_MARKERS));
    }

    // region Test With BackStepStrategy
    @Test
    public void testBackAllowed_From2() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // Step 2 should be able to skip backward and should go to step 1.
        Step previousStep = STRATEGY_TEST_NAVIGATOR.getPreviousStep(STRATEGY_TEST_STEPS.get(2), taskResult);
        assertNotNull(previousStep);
        assertEquals("step1", previousStep.getIdentifier());
    }

    @Test
    public void testBackAllowed_From3() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // Step 3 allows skipping back so we should go to step2.
        Step previousStep = STRATEGY_TEST_NAVIGATOR.getPreviousStep(STRATEGY_TEST_STEPS.get(3), taskResult);
        assertNotNull(previousStep);
        assertEquals("step2", previousStep.getIdentifier());
    }

    @Test
    public void testBackNotAllowed_From7() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }
        for (int i = 4; i < 7; i++) {
            stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(i), 0, 3));
        }
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // Step 7 doesn't allow going backward so we should get null
        Step previousStep = STRATEGY_TEST_NAVIGATOR.getPreviousStep(STRATEGY_TEST_STEPS.get(7), taskResult);
        assertNull(previousStep);
    }
    // endregion

    @Test
    public void testNext_From3() {
        TaskResult taskResult = mockTaskResult("task", STRATEGY_TEST_STEPS.subList(0, 3));
        // Step 3 has a next rule which returns "step1" so we should move on to step1.
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(STRATEGY_TEST_STEPS.get(3), taskResult);
        assertNotNull(nextStep);
        assertEquals("step1", nextStep.getIdentifier());
    }
    // endregion

    @Test
    public void testNext_FromIntroduction() {
        TaskResult taskResult = mockTaskResult("task", new ArrayList<Step>());
        // The introduction step has a next rule which returns "step2" so we should move on to step2.
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(STRATEGY_TEST_STEPS.get(0), taskResult);
        assertNotNull(nextStep);
        assertEquals("step2", nextStep.getIdentifier());
    }

    @Test
    public void testNext_from5Y() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(4), 0, 3));
        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(5), 0, 1));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // step5.Y has a next rule returns "step7" so we should move on to step7.
        SectionStep step5 = (SectionStep) STRATEGY_TEST_STEPS.get(5);
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(step5.getSteps().get(1), taskResult);
        assertNotNull(nextStep);
        assertEquals("step7", nextStep.getIdentifier());
    }

    @Test
    public void testNext_from5Z() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(4), 0, 3));
        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(5), 0, 2));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // step5.Z has a next rule returns "step4.B" so we should move on to step4.B.
        SectionStep step5 = (SectionStep) STRATEGY_TEST_STEPS.get(5);
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(step5.getSteps().get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step4.B", nextStep.getIdentifier());
    }

    @Test
    public void testNext_from7() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(4), 0, 3));
        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(5), 0, 3));
        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(6), 0, 3));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // step7 has a next rule returns "step6.A" so we should move on to step6.A.
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(STRATEGY_TEST_STEPS.get(7), taskResult);
        assertNotNull(nextStep);
        assertEquals("step6.A", nextStep.getIdentifier());
    }
    // endregion

    // region Test With NextStepStrategy
    @Test
    public void testNext_fromStep2() {
        TaskResult taskResult = mockTaskResult("task", STRATEGY_TEST_STEPS.subList(0, 2));
        // Step 2 returns null for it's next step identifier so the default behaviour should kick in and we should
        // move on to step3.
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(STRATEGY_TEST_STEPS.get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step3", nextStep.getIdentifier());
    }

    // region Test getProgress()
    @Test
    public void testProgess_NoMarkers_FlatHierarchy() {
        List<Step> steps = createSteps(new String[]{"1", "2", "3", "4"});
        StrategyBasedNavigator navigator = new StrategyBasedNavigator(steps, null);
        TaskResult taskResult = mockTaskResult("task", steps.subList(0, 1));

        TaskProgress progress = navigator.getProgress(steps.get(1), taskResult);
        assertNotNull(progress);
        assertEquals(2, progress.getProgress());
        assertEquals(4, progress.getTotal());
        assertTrue(progress.isEstimated());
    }

    @Test
    public void testSkip_resultNotPresent_From2() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // From step1, step2 shouldn't be skipped so we should end up there.
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(STRATEGY_TEST_STEPS.get(1), taskResult);
        assertNotNull(nextStep);
        assertEquals("step2", nextStep.getIdentifier());
    }
    // endregion

    @Test
    public void testSkip_resultNotPresent_From5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(4), 0, 2));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // From step4.C, we should go directly to step5.X since this step shouldn't be skipped.
        SectionStep step4 = (SectionStep) STRATEGY_TEST_STEPS.get(4);
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(step4.getSteps().get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step5.X", nextStep.getIdentifier());
    }

    // region Test With SkipStepStrategy
    @Test
    public void testSkip_resultPresent_From2() {
        List<Result> stepHistory = new ArrayList<>();
        stepHistory.add(mockTaskResult(SKIP_RESULT_IDENTIFIER, null));
        for (int i = 0; i < 2; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // From step1 we should go directly to step 3 since step 2 should be skipped.
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(STRATEGY_TEST_STEPS.get(1), taskResult);
        assertNotNull(nextStep);
        assertEquals("step3", nextStep.getIdentifier());
    }

    @Test
    public void testSkip_resultPresent_From5X() {
        List<Result> stepHistory = new ArrayList<>();
        stepHistory.add(mockTaskResult(SKIP_RESULT_IDENTIFIER, new ArrayList<Step>()));
        for (int i = 0; i < 4; i++) {
            stepHistory.add(mockResult(STRATEGY_TEST_STEPS.get(i)));
        }

        stepHistory.add(mockSectionResult(STRATEGY_TEST_STEPS.get(4), 0, 2));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        // From step4.C, we should go directly to step5.Y since step5.X should be skipped.
        SectionStep step4 = (SectionStep) STRATEGY_TEST_STEPS.get(4);
        Step nextStep = STRATEGY_TEST_NAVIGATOR.getNextStep(step4.getSteps().get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step5.Y", nextStep.getIdentifier());
    }

    private TaskResult mockSectionResult(Step step, int from, int to) {
        SectionStep sectionStep = (SectionStep) step;
        return mockTaskResult(sectionStep.getIdentifier(), sectionStep.getSteps().subList(from, to));
    }

    // region Mocking
    private static Step mockTestStep(String identifier, boolean isBackAllowed, String nextStepId,
            boolean shouldAddSkipRule) {
        Step step = mock(Step.class, withSettings().extraInterfaces(NextStepStrategy.class,
                BackStepStrategy.class, SkipStepStrategy.class));
        when(step.getIdentifier()).thenReturn(identifier);
        when(step.getType()).thenReturn(TEST_STEP_TYPE);
        when(step.toString()).thenReturn(identifier + ": " + TEST_STEP_TYPE);
        when(((BackStepStrategy) step).isBackAllowed(any(TaskResult.class))).thenReturn(isBackAllowed);
        when(((NextStepStrategy) step).getNextStepIdentifier(any(TaskResult.class))).thenReturn(nextStepId);
        if (shouldAddSkipRule) {
            // If we are adding a skip rule, return true for any argument in which the given TaskResult contains a
            // result with the SKIP_RESULT_IDENTIFIER, false otherwise.
            TaskResult match = argThat(new ArgumentMatcher<TaskResult>() {
                @Override
                public boolean matches(final TaskResult argument) {
                    return argument != null && argument.getResult(SKIP_RESULT_IDENTIFIER) != null;
                }
            });
            when(((SkipStepStrategy) step).shouldSkip(match)).thenReturn(true);
            TaskResult notMatch = argThat(new ArgumentMatcher<TaskResult>() {
                @Override
                public boolean matches(final TaskResult argument) {
                    return argument != null && argument.getResult(SKIP_RESULT_IDENTIFIER) == null;
                }
            });
            when(((SkipStepStrategy) step).shouldSkip(notMatch)).thenReturn(false);
        } else {
            // If we aren't adding a skip rule we always return false.
            when(((SkipStepStrategy) step).shouldSkip(any(TaskResult.class))).thenReturn(false);
        }

        return step;
    }

    static {
        STRATEGY_TEST_STEPS = new ArrayList<>();
        STRATEGY_TEST_STEPS.add(mockTestStep("introduction", true, "step2", false));
        STRATEGY_TEST_STEPS.add(mockStep("step1"));
        STRATEGY_TEST_STEPS.add(mockTestStep("step2", true, null, true));
        STRATEGY_TEST_STEPS.add(mockTestStep("step3", true, "step1", false));
        STRATEGY_TEST_STEPS.add(mockSectionStep("step4", createSteps(new String[]{"step4.A", "step4.B", "step4.c"})));
        List<Step> step5Substeps = new ArrayList<>();
        step5Substeps.add(mockTestStep("step5.X", true, null, true));
        step5Substeps.add(mockTestStep("step5.Y", true, "step7", false));
        step5Substeps.add(mockTestStep("step5.Z", true, "step4.B", false));
        STRATEGY_TEST_STEPS.add(mockSectionStep("step5", step5Substeps));
        STRATEGY_TEST_STEPS.add(mockSectionStep("step6", createSteps(new String[]{"step6.A", "step6.B", "step6.c"})));
        STRATEGY_TEST_STEPS.add(mockTestStep("step7", false, "step6.A", false));
        STRATEGY_TEST_STEPS.add(mockStep("conclusion"));
        STRATEGY_TEST_NAVIGATOR = new StrategyBasedNavigator(STRATEGY_TEST_STEPS, TEST_PROGRESS_MARKERS);
    }
    // endregion
}