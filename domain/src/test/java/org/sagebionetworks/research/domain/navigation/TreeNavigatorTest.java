package org.sagebionetworks.research.domain.navigation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.domain.task.navigation.TreeNavigator;

import java.util.Arrays;
import java.util.List;

public class TreeNavigatorTest extends IndividualNavigatorTest {

    public TreeNavigatorTest() {
        super(new TreeNavigator(TEST_STEPS, TEST_PROGRESS_MARKERS));
    }

    // region Test getProgress()
    @Test
    public void testProgess_NoMarkers_FlatHierarchy() {
        List<Step> steps = createSteps(new String[]{"1", "2", "3", "4"});
        TreeNavigator navigator = new TreeNavigator(steps, null);
        TaskResult taskResult = mockTaskResult("task", steps.subList(0, 1));

        TaskProgress progress = navigator.getProgress(steps.get(1), taskResult);
        assertNotNull(progress);
        assertEquals(2, progress.getProgress());
        assertEquals(4, progress.getTotal());
        assertTrue(progress.isEstimated());
    }
    // endregion
    @Test
    public void testGetStep_OneSectionDeep() {
        List<Step> steps1 = createSteps(new String[]{"1_a", "1_b"});
        List<Step> steps2 = createSteps(new String[]{"2_a", "2_b", "3_c"});
        List<Step> steps = Arrays.asList(new Step[] {
                mockSectionStep("1", steps1),
                mockSectionStep("2", steps2) });

        TreeNavigator navigator = new TreeNavigator(steps, null);

        Step section1 = navigator.getStep("1");
        assertNotNull(section1);
        assertEquals("1", section1.getIdentifier());

        Step section2 = navigator.getStep("2");
        assertNotNull(section2);
        assertEquals("2", section2.getIdentifier());

        Step firstAStep = navigator.getStep("a");
        assertNotNull(firstAStep);
        assertEquals("1_a", firstAStep.getIdentifier());

        Step firstBStep = navigator.getStep("b");
        assertNotNull(firstBStep);
        assertEquals("1_b", firstBStep.getIdentifier());

        // "c" fails the upwards navigation sub-step format because it's step identifier
        // implies that it's section step should have identifier "3", but it actually has identifier "2"
        Step cStepNull = navigator.getStep("c");
        assertNull(cStepNull);

        Step cStep = navigator.getStep("3_c");
        assertNotNull(cStep);
        assertEquals("3_c", cStep.getIdentifier());
    }

    @Test
    public void testGetStep_TwoSectionsDeep() {
        List<Step> steps1_3 = createSteps(new String[]{"1_3_a", "1_3_b"});
        List<Step> steps2_4 = createSteps(new String[]{"2_4_a", "2_4_b", "1_4_c", "2_3_e"});

        Step sectionStep1 = mockSectionStep("1", Arrays.asList(
                mockSectionStep("1_3", steps1_3),
                mockStep("1_d")));

        Step sectionStep2 = mockSectionStep("2", Arrays.asList(
                mockSectionStep("2_4", steps2_4),
                mockStep("2_d")));

        List<Step> steps = Arrays.asList(sectionStep1, sectionStep2, mockStep("5"));

        TreeNavigator navigator = new TreeNavigator(steps, null);

        Step section1 = navigator.getStep("1");
        assertNotNull(section1);
        assertEquals("1", section1.getIdentifier());

        Step section2 = navigator.getStep("2");
        assertNotNull(section2);
        assertEquals("2", section2.getIdentifier());

        Step section3 = navigator.getStep("3");
        assertNotNull(section3);
        assertEquals("1_3", section3.getIdentifier());

        Step section4 = navigator.getStep("4");
        assertNotNull(section4);
        assertEquals("2_4", section4.getIdentifier());

        Step step_1_3_a = navigator.getStep("a");
        assertNotNull(step_1_3_a);
        assertEquals("1_3_a", step_1_3_a.getIdentifier());

        Step step_2_4_a = navigator.getStep("2_4_a");
        assertNotNull(step_2_4_a);
        assertEquals("2_4_a", step_2_4_a.getIdentifier());

        Step step_1_3_b = navigator.getStep("b");
        assertNotNull(step_1_3_b);
        assertEquals("1_3_b", step_1_3_b.getIdentifier());

        Step step_2_4_b = navigator.getStep("2_4_b");
        assertNotNull(step_2_4_b);
        assertEquals("2_4_b", step_2_4_b.getIdentifier());

        // "e" fails the upwards navigation sub-step format because it's step identifier
        // implies that it's section step should have identifier "4", but it actually has identifier "3"
        Step dStepNull = navigator.getStep("e");
        assertNull(dStepNull);

        Step step_1_d = navigator.getStep("d");
        assertNotNull(step_1_d);
        assertEquals("1_d", step_1_d.getIdentifier());

        Step step_2_3_e = navigator.getStep("2_3_e");
        assertNotNull(step_2_3_e);
        assertEquals("2_3_e", step_2_3_e.getIdentifier());
    }
}

