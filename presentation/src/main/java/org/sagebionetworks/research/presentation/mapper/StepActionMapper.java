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

package org.sagebionetworks.research.presentation.mapper;


import org.sagebionetworks.research.domain.step.StepAction;
import org.sagebionetworks.research.domain.step.StepAction.StepCustomAction;
import org.sagebionetworks.research.domain.step.StepAction.StepNavigationAction;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.R;
import org.sagebionetworks.research.presentation.model.StepActionView;
import org.sagebionetworks.research.presentation.model.action.ActionType;

import javax.inject.Inject;

public class StepActionMapper {
    @Inject
    public StepActionMapper() {
    }

    StepActionView create(StepAction stepAction) {
        String actionType = null;
        DisplayString displayString = null;
        int iconRes = 0;         // TODO: implement default iconRes

        switch (stepAction.actionType) {
            case CUSTOM:
                StepCustomAction stepCustomAction = (StepCustomAction) stepAction;
                displayString = DisplayString.create(R.string.rs2_empty,
                        stepAction.title);
                actionType = stepCustomAction.customActionType;
                break;
            case NAVIGATION:
                StepNavigationAction stepNavigationAction = (StepNavigationAction) stepAction;
                switch (stepNavigationAction.navigationActionType) {
                    // TODO: implement other actions
                    case FORWARD:
                        displayString = DisplayString.create(R.string.rs2_navigation_action_forward,
                                stepNavigationAction.title);
                        actionType = ActionType.FORWARD;
                }
        }

        if (displayString == null) {
            throw new IllegalArgumentException("Unknown stepAction: " + stepAction);
        }

        return new StepActionView(actionType, displayString, iconRes, false, true);
    }
}
