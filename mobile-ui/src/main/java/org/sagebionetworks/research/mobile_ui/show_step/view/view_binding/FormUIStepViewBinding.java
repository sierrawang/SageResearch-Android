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

import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.sagebionetworks.research.domain.form.implementations.ChoiceInputField;
import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.domain.mobile_ui.R2;
import org.sagebionetworks.research.domain.mobile_ui.R2.id;
import org.sagebionetworks.research.domain.step.interfaces.FormUIStep;
import org.sagebionetworks.research.mobile_ui.show_step.view.form.FormUIAdapter;
import org.sagebionetworks.research.presentation.model.form.ChoiceInputFieldViewBase;
import org.sagebionetworks.research.presentation.model.form.ChoiceView;
import org.sagebionetworks.research.presentation.model.form.InputFieldView;
import org.sagebionetworks.research.presentation.model.interfaces.FormUIStepView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FormUIStepViewBinding<S extends FormUIStepView> extends UIStepViewBinding<S> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormUIStep.class);

    private final Unbinder unbinder;
    protected final FormUIStepViewHolder formUIViewHolder;

    public FormUIStepViewBinding(View view) {
        super(view);
        this.formUIViewHolder = new FormUIStepViewHolder();
        this.unbinder = ButterKnife.bind(this.formUIViewHolder, view);
    }

    @Override
    public void unbind() {
        super.unbind();
        this.unbinder.unbind();
    }

    public RecyclerView getRecyclerView() {
        return this.formUIViewHolder.recyclerView;
    }

    protected static final class FormUIStepViewHolder {
        @BindView(id.rs2_recycler_view)
        RecyclerView recyclerView;
    }
}
