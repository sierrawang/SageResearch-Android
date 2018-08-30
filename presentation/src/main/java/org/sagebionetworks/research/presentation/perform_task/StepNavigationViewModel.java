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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.task.navigation.StepNavigator;
import org.sagebionetworks.research.domain.task.navigation.TaskProgress;

/**
 * Accepts navigation actions. Observes latest TaskResult and provides LiveData access to navigator state.
 */
public class StepNavigationViewModel {

    private final LiveData<Step> backwardStepLiveData;

    private final MutableLiveData<Step> currentStepLiveData;

    private final LiveData<Step> forwardStepLiveData;

    private final LiveData<Boolean> hasBackwardStepLiveData;

    private final LiveData<Boolean> hasForwardStepLiveData;

    private final LiveData<TaskProgress> taskProgressStepLiveData;

    private final LiveData<TaskResult> trLiveData;

    public StepNavigationViewModel(final StepNavigator stepNavigator, final LiveData<TaskResult> taskResultLiveData) {
        currentStepLiveData = new MutableLiveData<>();
        currentStepLiveData.setValue(null);
        this.trLiveData=taskResultLiveData;

        forwardStepLiveData = Transformations.switchMap(taskResultLiveData,
                tr -> Transformations.map(currentStepLiveData, s -> stepNavigator.getNextStep(s, tr)));
        hasForwardStepLiveData = Transformations.map(forwardStepLiveData, n -> (n != null));

        backwardStepLiveData = Transformations.switchMap(taskResultLiveData,
                tr -> Transformations.map(currentStepLiveData, s -> stepNavigator.getPreviousStep(s, tr)));
        hasBackwardStepLiveData = Transformations.map(backwardStepLiveData, n -> (n != null));

        taskProgressStepLiveData = Transformations.switchMap(taskResultLiveData,
                tr -> Transformations.map(currentStepLiveData, s -> stepNavigator.getProgress(s, tr)));
    }

    public LiveData<Step> getBackwardStepLiveData() {
        return backwardStepLiveData;
    }

    public MutableLiveData<Step> getCurrentStepLiveData() {
        return currentStepLiveData;
    }

    public LiveData<Step> getForwardStepLiveData() {
        return forwardStepLiveData;
    }

    public LiveData<TaskProgress> getTaskProgressLiveData() {
        return taskProgressStepLiveData;
    }

    public void goBackward() {
        currentStepLiveData.setValue(backwardStepLiveData.getValue());
    }

    public void goForward() {
        currentStepLiveData.setValue(forwardStepLiveData.getValue());
    }

    public LiveData<Boolean> hasBackwardStepLiveData() {
        return hasBackwardStepLiveData;
    }

    public LiveData<Boolean> hasForwardStepLiveData() {
        return hasForwardStepLiveData;
    }
}
