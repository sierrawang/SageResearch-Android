package org.sagebionetworks.research.domain.navigation;

import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.BackStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.NextStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.SkipStepStrategy;

public interface TestStep extends Step, NextStepStrategy, BackStepStrategy, SkipStepStrategy { }
