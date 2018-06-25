package org.sagebionetworks.research.domain.navigation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;
import org.sagebionetworks.research.domain.task.navigation.TreeNavigator;

import java.util.List;

public class TreeNavigatorTests extends IndividualNavigatorTests {

    public TreeNavigatorTests() {
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
}

