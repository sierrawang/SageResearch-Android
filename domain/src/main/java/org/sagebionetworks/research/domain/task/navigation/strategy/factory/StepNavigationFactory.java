package org.sagebionetworks.research.domain.task.navigation.strategy.factory;

import com.google.common.collect.Lists;

import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.NextStepStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.factory.json.StepNavigationStrategy;
import org.sagebionetworks.research.domain.task.navigation.strategy.factory.next_step.ConstantNextStepStrategy;

import java.lang.reflect.Proxy;
import java.util.List;

public class StepNavigationFactory {

    @SuppressWarnings("unchecked")
    public <T extends Step> T create(T step, StepNavigationStrategy stepNavigationStrategy) {

        List<Class> interfaces = Lists.newArrayList(step.getClass());
        List<Object> delegates = Lists.newArrayList(step);

        if (stepNavigationStrategy.hasNextStepIdentifier()) {
            ConstantNextStepStrategy nextStepStrategy = new ConstantNextStepStrategy(
                    stepNavigationStrategy.getNextStepIdentifier());
            interfaces.add(NextStepStrategy.class);
            delegates.add(nextStepStrategy);
        }

        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{Step.class}, new Delegator(new Class[]{Step.class}, new Object[]{step}));
    }
}
