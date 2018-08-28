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

package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.StepViewBinding;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.ShowGenericStepViewModel;

/**
 * Shows a simple step.
 */
public class ShowGenericStepFragment extends ShowStepFragmentBase<StepView, ShowGenericStepViewModel,
        StepViewBinding<StepView>> {
    private static final String ARGUMENT_STEP_VIEW = "STEP_VIEW";

    @NonNull
    public static ShowGenericStepFragment newInstance(@NonNull StepView stepView) {
        ShowGenericStepFragment fragment = new ShowGenericStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ShowGenericStepFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getActionTypeFromActionButton(@NonNull final ActionButton actionButton) {
        int actionButtonId = actionButton.getId();

        String actionType = null;
        if (R.id.rs2_step_navigation_action_forward == actionButtonId) {
            return ActionType.FORWARD;
        } else if (R.id.rs2_step_navigation_action_backward == actionButtonId) {
            return ActionType.BACKWARD;
        }

        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.rs2_default_show_step_fragment_layout;
    }

    @Override
    protected UIStepViewBinding instantiateAndBindBinding(View view) {
        return new UIStepViewBinding(view);
    }
}
