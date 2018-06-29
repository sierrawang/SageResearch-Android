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

package org.sagebionetworks.research.mobile_ui.show_step.view.view_binding;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R2.id;
import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * An ActiveUIStepViewBinding is a view binding that has everything a UIStepViewBinding and the following optional
 * bindings countdownDial : ProgressBar - The countdown dial which indicates to the user how long they should perform
 * the active task for. countdownLabel : TextView - The label to display the text corresponding to the countdownDial's
 * progress on. unitLabel : TextView - The label to display the unit that the countdown is occurring in on.
 */
public class ActiveUIStepViewBinding<S extends ActiveUIStepView> extends UIStepViewBinding<S> {
    protected static class ActiveUIStepViewHolder {
        /**
         * The binding can optionally contain a progress bar which displays a visual representation of the progress
         * toward finishing the active step.
         */
        @BindView(id.countdownDial)
        @Nullable
        public ProgressBar countdownDial;

        /**
         * The binding can optionally have a TextView which displays a text representation of the progress toward
         * finishing the active step.
         */
        @BindView(id.countLabel)
        @Nullable
        public TextView countdownLabel;

        /**
         * The binding can optionally have a TextView which displays the unit that the countdown is occurring in.
         */
        @BindView(id.unitLabel)
        public TextView unitLabel;
    }

    private final ActiveUIStepViewHolder activeUIStepViewHolder;

    private final Unbinder activeUIStepViewHolderUnbinder;

    public ActiveUIStepViewBinding(View view) {
        super(view);
        this.activeUIStepViewHolder = new ActiveUIStepViewHolder();
        this.activeUIStepViewHolderUnbinder = ButterKnife.bind(this.activeUIStepViewHolder, view);
    }

    // TODO rkolmos 06/10/2018 potentially override update();

    @Override
    public void unbind() {
        super.unbind();
        this.activeUIStepViewHolderUnbinder.unbind();
    }

    // TODO rkolmos 05/25/2018 override update to do the correct thing once the corresponding subclass of StepView is
    // created
}
