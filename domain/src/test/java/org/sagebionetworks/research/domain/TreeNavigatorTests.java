package org.sagebionetworks.research.domain;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.sagebionetworks.research.domain.result.Result;
import org.sagebionetworks.research.domain.result.TaskResult;
import org.sagebionetworks.research.domain.step.SectionStep;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.navigation.TreeNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreeNavigatorTests {
    private static final String TEST_STEP_TYPE = "TEST_STEP";

    // region Mocking
    private static Step mockStep(String identifier, String type) {
        Step step = mock(Step.class);
        when(step.getIdentifier()).thenReturn(identifier);
        when(step.getType()).thenReturn(type);
        when(step.toString()).thenReturn(identifier + ": " + type);
        return step;
    }

    private static SectionStep mockSectionStep(String identifier ,List<Step> substeps) {
        SectionStep sectionStep = mock(SectionStep.class);
        when(sectionStep.getSteps()).thenReturn(substeps);
        when(sectionStep.getIdentifier()).thenReturn(identifier);
        String type = StepType.SECTION;
        when(sectionStep.getType()).thenReturn(type);
        when(sectionStep.toString()).thenReturn(identifier + ": " + type);
        return sectionStep;
    }

    private static List<Step> createSteps(String[] identifiers) {
        List<Step> steps = new ArrayList<>();
        for (String identifier : identifiers) {
            steps.add(mockStep(identifier, TEST_STEP_TYPE));
        }
        return steps;
    }

    private static Result mockResult(Step step) {
        Result result = mock(Result.class);
        String identifier = step.getIdentifier();
        when(result.getIdentifier()).thenReturn(identifier);
        String type = step.getType();
        when(result.getType()).thenReturn(type);
        return result;
    }

    private static TaskResult mockTaskResult(String identifier, List<Step> substeps) {
        List<Result> subResults = new ArrayList<>();
        for (Step step : substeps) {
            subResults.add(mockResult(step));
        }
        return mockTaskResultFromResults(identifier, subResults);
    }

    private static TaskResult mockTaskResultFromResults(String identifier, List<Result> subResults) {
        TaskResult taskResult = mock(TaskResult.class);
        for (Result stepResult : subResults) {
            when(taskResult.getResult(stepResult.getIdentifier())).thenReturn(stepResult);
        }

        ImmutableList.Builder<Result> builder = new ImmutableList.Builder<>();
        builder.addAll(subResults);
        ImmutableList<Result> stepHistory = builder.build();
        when(taskResult.getStepHistory()).thenReturn(stepHistory);
        when(taskResult.getIdentifier()).thenReturn(identifier);
        return taskResult;
    }
    // endregion

    private static final List<Step> TEST_STEPS;
    private static final List<String> TEST_PROGRESS_MARKERS;
    private static final TreeNavigator TEST_NAVIGATOR;
    static {
        List<Step> steps = new ArrayList<>(createSteps(new String[]{"introduction", "step1", "step2", "step3"}));
        steps.add(mockSectionStep("step4", createSteps(new String[]{"step4.A", "step4.B", "step4.C"})));
        steps.add(mockSectionStep("step5", createSteps(new String[]{"step5.X", "step5.Y", "step5.Z"})));
        steps.add(mockSectionStep("step6", createSteps(new String[]{"step6.A", "step6.B", "step6.C"})));
        steps.addAll(createSteps(new String[]{"step7", "completion"}));
        TEST_STEPS = steps;
        String[] progressMarkers = {"step1", "step2", "step3", "step4", "step5", "step6", "step7"};
        TEST_PROGRESS_MARKERS = Arrays.asList(progressMarkers);
        TEST_NAVIGATOR = new TreeNavigator(TEST_STEPS, TEST_PROGRESS_MARKERS);
    }

    // NOTE: For all tests except for the progress ones, creating a TaskResult shouldn't change the behavior of
    // the method being called. However, to ensure the test reflects the state the method will actually be called
    // in a TaskResult is created anyway.

    // region Test getProgress()
    @Test
    public void testProgess_NoMarkers_FlatHierarchy() {
        List<Step> steps = createSteps(new String[]{"1", "2", "3", "4"});
        TreeNavigator navigator = new TreeNavigator(steps, null);
        TaskResult taskResult = mockTaskResult("task", steps.subList(0, 1));

        Task.Progress progress = navigator.getProgress(steps.get(1), taskResult);
        assertNotNull(progress);
        assertEquals(2, progress.getProgress());
        assertEquals(4, progress.getTotal());
        assertTrue(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Introduction() {
        TaskResult taskResult = mockTaskResult("task", new ArrayList<Step>());
        Task.Progress progress = TEST_NAVIGATOR.getProgress(TEST_STEPS.get(0), taskResult);
        assertNull(progress);
    }

    @Test
    public void testProgress_MarkersAndSections_Step2() {
        Step step = TEST_STEPS.get(2);
        TaskResult taskResult = mockTaskResult("task", TEST_STEPS.subList(0, 2));
        Task.Progress progress = TEST_NAVIGATOR.getProgress(step, taskResult);
        assertNotNull(progress);
        assertEquals(2, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step7() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps()));
        SectionStep step6 = (SectionStep) TEST_STEPS.get(6);
        stepHistory.add(mockTaskResult(step6.getIdentifier(), step6.getSteps()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Task.Progress progress = TEST_NAVIGATOR.getProgress(TEST_STEPS.get(7), taskResult);
        assertNotNull(progress);
        assertEquals(7, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step4B() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        List<Step> step4Substeps = step4.getSteps();
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4Substeps.subList(0, 1)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Task.Progress progress = TEST_NAVIGATOR.getProgress(step4.getSteps().get(1), taskResult);
        assertNotNull(progress);
        assertEquals(4, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), new ArrayList<Step>()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Task.Progress progress = TEST_NAVIGATOR.getProgress(step5.getSteps().get(0), taskResult);
        assertNotNull(progress);
        assertEquals(5, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Step6C() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps()));
        SectionStep step6 = (SectionStep) TEST_STEPS.get(6);
        stepHistory.add(mockTaskResult(step6.getIdentifier(), step6.getSteps().subList(0, 2)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Task.Progress progress = TEST_NAVIGATOR.getProgress(step6.getSteps().get(2), taskResult);
        assertNotNull(progress);
        assertEquals(6, progress.getProgress());
        assertEquals(7, progress.getTotal());
        assertFalse(progress.isEstimated());
    }

    @Test
    public void testProgress_MarkersAndSections_Completion() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps()));
        SectionStep step6 = (SectionStep) TEST_STEPS.get(6);
        stepHistory.add(mockTaskResult(step6.getIdentifier(), step6.getSteps()));
        stepHistory.add(mockResult(TEST_STEPS.get(7)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Task.Progress progress = TEST_NAVIGATOR.getProgress(TEST_STEPS.get(8), taskResult);
        assertNull(progress);
    }
    // endregion

    // region Test getPreviousStep()
    @Test
    public void testBack_From5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), new ArrayList<Step>()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step previousStep = TEST_NAVIGATOR.getPreviousStep(step5.getSteps().get(0), taskResult);
        assertNotNull(previousStep);
        assertEquals("step4.C", previousStep.getIdentifier());
    }

    @Test
    public void testBack_From5Z() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps().subList(0, 2)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step previousStep = TEST_NAVIGATOR.getPreviousStep(step5.getSteps().get(2), taskResult);
        assertNotNull(previousStep);
        assertEquals("step5.Y", previousStep.getIdentifier());
    }

    @Test
    public void testBack_From3() {
        TaskResult taskResult = mockTaskResult("task", TEST_STEPS.subList(1, 3));
        Step previousStep = TEST_NAVIGATOR.getPreviousStep(TEST_STEPS.get(3), taskResult);
        assertNotNull(previousStep);
        assertEquals("step2", previousStep.getIdentifier());
    }
    // endregion

    // region Test getNextStep()
    @Test
    public void testForward_From5X() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), new ArrayList<Step>()));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step nextStep = TEST_NAVIGATOR.getNextStep(step5.getSteps().get(0), taskResult);
        assertNotNull(nextStep);
        assertEquals("step5.Y", nextStep.getIdentifier());
    }

    @Test
    public void testForward_From5Z() {
        List<Result> stepHistory = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stepHistory.add(mockResult(TEST_STEPS.get(i)));
        }

        SectionStep step4 = (SectionStep) TEST_STEPS.get(4);
        stepHistory.add(mockTaskResult(step4.getIdentifier(), step4.getSteps()));
        SectionStep step5 = (SectionStep) TEST_STEPS.get(5);
        stepHistory.add(mockTaskResult(step5.getIdentifier(), step5.getSteps().subList(0, 2)));
        TaskResult taskResult = mockTaskResultFromResults("task", stepHistory);
        Step nextStep = TEST_NAVIGATOR.getNextStep(step5.getSteps().get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step6.A", nextStep.getIdentifier());
    }

    @Test
    public void testForward_From2() {
        TaskResult taskResult = mockTaskResult("task", TEST_STEPS.subList(0, 2));
        Step nextStep = TEST_NAVIGATOR.getNextStep(TEST_STEPS.get(2), taskResult);
        assertNotNull(nextStep);
        assertEquals("step3", nextStep.getIdentifier());
    }
    // endregion

    // region Test getStep()
    @Test
    public void testGetStep() {
        assertEquals(TEST_STEPS.get(0), TEST_NAVIGATOR.getStep("introduction"));
        assertEquals(((SectionStep)TEST_STEPS.get(4)).getSteps().get(2), TEST_NAVIGATOR.getStep("step4.C"));
        assertEquals(TEST_STEPS.get(7), TEST_NAVIGATOR.getStep("step7"));
        assertEquals(TEST_STEPS.get(5), TEST_NAVIGATOR.getStep("step5"));
    }
    // endregion
}
