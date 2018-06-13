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

package org.sagebionetworks.research.domain.task.navigation.strategy.next_step;

import org.junit.*;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.implementations.UIStepBase;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.strategy.StepNavigationStrategy.NextStepStrategy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NextStepStrategyFactoryTest {

    private NextStepStrategyFactory nextStepStrategyFactory;

    @Before
    public void setup() {
        nextStepStrategyFactory = new NextStepStrategyFactory();
    }

    @Test
    public void createWithNextStepStrategy() {
        String nextIdentifier = "nextIdentifier";
        TaskResult taskResult = mock(TaskResult.class);

        NextStepStrategy nextStepStrategy = mock(NextStepStrategy.class);
        when(nextStepStrategy.getNextStepIdentifier(taskResult)).thenReturn(nextIdentifier);

        Step step = new UIStepBase("identifier", null, null, null, null, null, null, null);

        NextStepStrategy nextStepStrategyResult = nextStepStrategyFactory.create(step, nextStepStrategy);

        assertEquals(nextIdentifier, nextStepStrategyResult.getNextStepIdentifier(taskResult));

        verify(nextStepStrategy).getNextStepIdentifier(taskResult);
    }
    @Test
    public void createWithNextStepIdentifier() {
        Step step = new UIStepBase("identifier", null, null, null, null, null, null, null);
        String nextIdentifier = "nextIdentifier";

        NextStepStrategy nextStepStrategy = nextStepStrategyFactory.create(step, nextIdentifier);

        assertEquals(nextIdentifier, nextStepStrategy.getNextStepIdentifier(null));
    }
}