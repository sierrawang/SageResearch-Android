package org.sagebionetworks.research.domain.navigation;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.*;
import org.sagebionetworks.research.domain.task.navigation.strategy.StrategyBasedNavigator;

@RunWith(Suite.class)
@SuiteClasses({ TreeNavigatorTests.class, StrategyBasedNavigatorTests.class })
public class AllNavigatorTests {

}
