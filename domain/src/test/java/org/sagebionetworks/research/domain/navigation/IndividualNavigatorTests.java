package org.sagebionetworks.research.domain.navigation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.sagebionetworks.research.domain.result.ResultType;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class IndividualNavigatorTests {
    private static final String STEP_TYPE = "step";

    protected static final List<Step> TEST_STEPS;

    protected static final List<String> TEST_PROGRESS_MARKERS;

    protected final StepNavigator navigator;

    private final List<Step> steps;

    public static List<Step> createSteps(String[] identifiers) {
        List<Step> steps = new ArrayList<>();
        for (String identifier : identifiers) {
            steps.add(mockStep(identifier));
        }
        return steps;
    }

    public static Result mockResult(String identifier) {
        Result result = mock(Result.class);
        when(result.getIdentifier()).thenReturn(identifier);
        when(result.getType()).thenReturn(ResultType.BASE);
        return result;
    }

    public static Result mockResult(Step step) {
        Result result = mock(Result.class);
        String identifier = step.getIdentifier();
        when(result.getIdentifier()).thenReturn(identifier);
        when(result.getType()).thenReturn(ResultType.BASE);
        return result;
    }

    public static SectionStep mockSectionStep(String identifier, List<Step> substeps) {
        SectionStep sectionStep = mock(SectionStep.class);
        when(sectionStep.getSteps()).thenReturn(ImmutableList.copyOf(substeps));
        when(sectionStep.getIdentifier()).thenReturn(identifier);
        String type = StepType.SECTION;
        when(sectionStep.getType()).thenReturn(type);
        when(sectionStep.toString()).thenReturn(identifier + ": " + type);
        return sectionStep;
    }

    // region Mocking
    public static Step mockStep(String identifier) {
        Step step = mock(Step.class);
        when(step.getIdentifier()).thenReturn(identifier);
        when(step.getType()).thenReturn(STEP_TYPE);
        when(step.toString()).thenReturn(identifier + ": " + STEP_TYPE);
        return step;
    }

    public static TaskResult mockTaskResult(String identifier, List<Step> substeps) {
        List<Result> subResults = new ArrayList<>();
        if (substeps != null) {
            for (Step step : substeps) {
                subResults.add(mockResult(step));
            }
        }
        return mockTaskResultFromResults(identifier, subResults);
    }

    public static TaskResult mockTaskResultFromResults(String identifier, List<Result> subResults) {
        TaskResult taskResult = mock(TaskResult.class);
        for (Result stepResult : subResults) {
            String resultIdentifier = stepResult.getIdentifier();
            when(taskResult.getResult(resultIdentifier)).thenReturn(stepResult);
        }

        ImmutableList.Builder<Result> builder = new ImmutableList.Builder<>();
        builder.addAll(subResults);
        ImmutableList<Result> stepHistory = builder.build();
        when(taskResult.getStepHistory()).thenReturn(stepHistory);
        when(taskResult.getIdentifier()).thenReturn(identifier);
        return taskResult;
    }

    public IndividualNavigatorTests(StepNavigator navigator) {
        this.navigator = navigator;
        this.steps = TEST_STEPS;
    }

    @Test
    public void testBack_From3() {
        TaskResult taskResult = mockTaskResult("task", steps.subList(1, 3));
        Step previousStep = navigator.getPreviousStep(steps.get(3), taskResult);
        assertNotNull(previousStep);
        assertEquals("step2", previousStep.getIdentifier());
    }
    // endregion

    // NOTE: For all tests except for the progress ones, creating a TaskResult shouldn't change the behavior of
    // the method being called. However, to ensure the test reflects the state the method will actually be called
    // in a TaskResult is created anyway.

    // region Test getPreviousStep()
    @Test
    public void testBack_From5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), new ArrayList<Step>()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step previousStep = navigator.getPreviousStep(step5.getSteps().get(0), taskResult);
        assertNotNull(previousStep);
        assertEquals("step4.C", previousStep.getIdentifier());
    }

    @Test
    public void testBack_From5Z() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps().subList(0, 2)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step previousStep = navigator.getPreviousStep(step5.getSteps().get(2), taskResult);
        assertNotNull(previousStep);
        assertEquals("step5.Y", previousStep.getIdentifier());
    }

    @Test
    public void testForward_From2() {
        TaskResult taskResult = mockTaskResult("task", steps.subList(0, 2));
        Step nextStep = navigator.getNextStep(steps.get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step3", nextStep.getIdentifier());
    }

    // region Test getNextStep()
    @Test
    public void testForward_From5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), new ArrayList<Step>()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step nextStep = navigator.getNextStep(step5.getSteps().get(0), taskResult);
        assertNotNull(nextStep);
        assertEquals("step5.Y", nextStep.getIdentifier());
    }

    @Test
    public void testForward_From5Z() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps().subList(0, 2)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step nextStep = navigator.getNextStep(step5.getSteps().get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step6.A", nextStep.getIdentifier());
    }

    // region Test getStep()
    @Test
    public void testGetStep() {
        assertEquals(steps.get(0), navigator.getStep("introduction"));
        assertEquals(((SectionStep) steps.get(4)).getSteps().get(2), navigator.getStep("step4.C"));
        assertEquals(steps.get(7), navigator.getStep("step7"));
        assertEquals(steps.get(5), navigator.getStep("step5"));
    }

    @Test
    public void testProgress_MarkersAndSections_Completion() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps()));
        SectionStep step6 = (SectionStep) steps.get(6);
        stepHistory.add(mockTaskResult(step6.getIdentifier(), step6.getSteps()));
        stepHistory.add(mockResult(steps.get(7)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        TaskProgress progress = navigator.getProgress(steps.get(8), taskResult);
        assertNull(progress);
    }
    // endregion

    // region Test getProgress()
    @Test
    public void testProgress_MarkersAndSections_Introduction() {
        TaskResult taskResult = mockTaskResult("task", new ArrayList<Step>());
        TaskProgress progress = navigator.getProgress(steps.get(0), taskResult);
        assertNull(progress);
    }

    @Test
    public void testProgress_MarkersAndSections_Step2() {
        Step step = steps.get(2);
        TaskResult taskResult = mockTaskResult("task", steps.subList(0, 2));
        TaskProgress progress = navigator.getProgress(step, taskResult);
        assertNotNull(progress);
        assertEquals(2, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step4B() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        List<Step> step4Substeps = step4.getSteps();
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4Substeps.subList(0, 1)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        TaskProgress progress = navigator.getProgress(step4.getSteps().get(1), taskResult);
        assertNotNull(progress);
        assertEquals(4, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }
    // endregion

    @Test
    public void testProgress_MarkersAndSections_Step5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), new ArrayList<Step>()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        TaskProgress progress = navigator.getProgress(step5.getSteps().get(0), taskResult);
        assertNotNull(progress);
        assertEquals(5, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step6C() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps()));
        SectionStep step6 = (SectionStep) steps.get(6);
        stepHistory.add(mockTaskResult(step6.getIdentifier(), step6.getSteps().subList(0, 2)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        TaskProgress progress = navigator.getProgress(step6.getSteps().get(2), taskResult);
        assertNotNull(progress);
        assertEquals(6, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step7() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(steps.get(i)));
        }

        SectionStep step4 = (SectionStep) steps.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) steps.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps()));
        SectionStep step6 = (SectionStep) steps.get(6);
        stepHistory.add(mockTaskResult(step6.getIdentifier(), step6.getSteps()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        TaskProgress progress = navigator.getProgress(steps.get(7), taskResult);
        assertNotNull(progress);
        assertEquals(7, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }
    // endregion

    static {
        List<Step> steps = new ArrayList<>(createSteps(new String[]{"introduction", "step1", "step2", "step3"}));
        steps.add(mockSectionStep("step4", createSteps(new String[]{"step4.A", "step4.B", "step4.C"})));
        steps.add(mockSectionStep("step5", createSteps(new String[]{"step5.X", "step5.Y", "step5.Z"})));
        steps.add(mockSectionStep("step6", createSteps(new String[]{"step6.A", "step6.B", "step6.C"})));
        steps.addAll(createSteps(new String[]{"step7", "completion"}));
        TEST_STEPS = steps;
        String[] progressMarkers = {"step1", "step2", "step3", "step4", "step5", "step6", "step7"};
        TEST_PROGRESS_MARKERS = Arrays.asList(progressMarkers);
    }
    // endregion
}
