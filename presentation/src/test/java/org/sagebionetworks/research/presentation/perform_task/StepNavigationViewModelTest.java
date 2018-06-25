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

package org.sagebionetworks.research.presentation.perform_task;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;

public class StepNavigationViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private StepNavigationViewModel stepNavigationViewModel;

    @Mock
    private StepNavigator stepNavigator;

    private TaskResult taskResultBuilder;

    private MutableLiveData<TaskResult> taskResultMutableLiveData;

    @Test
    public void getCurrentStepLiveData() {
        Observer<Step> currentStepObs = mock(Observer.class);
        Observer<Step> previousStepObs = mock(Observer.class);
        Observer<Step> nextStepObs = mock(Observer.class);

        Step nextStep = mock(Step.class);
        Step nextStep2 = mock(Step.class);

        TaskResult taskResult = mock(TaskResult.class);

        when(stepNavigator.getNextStep(isNull(), eq(taskResult))).thenReturn(nextStep);

        stepNavigationViewModel.getCurrentStepLiveData().observeForever(currentStepObs);
        stepNavigationViewModel.getNextStepLiveData().observeForever(nextStepObs);
        stepNavigationViewModel.getPreviousStepLiveData().observeForever(previousStepObs);

        taskResultMutableLiveData.setValue(taskResult);

        verify(currentStepObs, atLeastOnce()).onChanged(null);
        verify(previousStepObs, atLeastOnce()).onChanged(null);
        verify(nextStepObs, atLeastOnce()).onChanged(nextStep);

        when(stepNavigator.getNextStep(eq(nextStep), eq(taskResult))).thenReturn(nextStep2);
        when(stepNavigator.getPreviousStep(eq(nextStep), eq(taskResult))).thenReturn(null);

        stepNavigationViewModel.goForward();

        verify(currentStepObs, atLeastOnce()).onChanged(nextStep);
        verify(previousStepObs, atLeastOnce()).onChanged(null);
        verify(nextStepObs, atLeastOnce()).onChanged(nextStep2);

        when(stepNavigator.getNextStep(eq(nextStep2), eq(taskResult))).thenReturn(null);
        when(stepNavigator.getPreviousStep(eq(nextStep2), eq(taskResult))).thenReturn(nextStep);

        stepNavigationViewModel.goForward();

        verify(currentStepObs, atLeastOnce()).onChanged(nextStep2);
        verify(previousStepObs, atLeastOnce()).onChanged(nextStep);
        verify(nextStepObs, atLeastOnce()).onChanged(null);

        stepNavigationViewModel.goBackward();

        verify(currentStepObs, atLeastOnce()).onChanged(nextStep);
        verify(previousStepObs, atLeastOnce()).onChanged(null);
        verify(nextStepObs, atLeastOnce()).onChanged(nextStep2);

        // Test a change in TaskResult
        reset(stepNavigator);

        TaskResult taskResult2 = mock(TaskResult.class);
        when(stepNavigator.getNextStep(eq(nextStep), eq(taskResult2))).thenReturn(null);
        when(stepNavigator.getPreviousStep(eq(nextStep), eq(taskResult2))).thenReturn(nextStep2);

        taskResultMutableLiveData.setValue(taskResult2);

        verify(currentStepObs, atLeastOnce()).onChanged(nextStep);
        verify(previousStepObs, atLeastOnce()).onChanged(nextStep2);
        verify(nextStepObs, atLeastOnce()).onChanged(null);

        verify(stepNavigator).getNextStep(eq(nextStep), eq(taskResult2));
        verify(stepNavigator).getPreviousStep(eq(nextStep), eq(taskResult2));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        taskResultMutableLiveData = new MutableLiveData<>();
        stepNavigationViewModel = new StepNavigationViewModel(stepNavigator, taskResultMutableLiveData);
    }
}