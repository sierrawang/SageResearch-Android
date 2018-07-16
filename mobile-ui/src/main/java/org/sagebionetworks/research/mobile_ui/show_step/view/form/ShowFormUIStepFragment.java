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

package org.sagebionetworks.research.mobile_ui.show_step.view.form;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Orientation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.FormUIStepViewBinding;
import org.sagebionetworks.research.presentation.model.form.ChoiceInputFieldViewBase;
import org.sagebionetworks.research.presentation.model.form.ChoiceView;
import org.sagebionetworks.research.presentation.model.form.InputFieldView;
import org.sagebionetworks.research.presentation.model.interfaces.FormUIStepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShowFormUIStepFragment extends
        ShowStepFragmentBase<FormUIStepView, ShowUIStepViewModel<FormUIStepView>,
                        FormUIStepViewBinding<FormUIStepView>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowFormUIStepFragment.class);

    @NonNull
    public static ShowFormUIStepFragment newInstance(@NonNull StepView stepView) {
        if (!(stepView instanceof FormUIStepView)) {
            throw new IllegalArgumentException("Step view: " + stepView + " is not a FormUIStepView.");
        }

        ShowFormUIStepFragment fragment = new ShowFormUIStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.rs2_form_step;
    }

    @NonNull
    @Override
    protected FormUIStepViewBinding<FormUIStepView> instantiateAndBindBinding(View view) {
        return new FormUIStepViewBinding<>(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView recyclerView = this.stepViewBinding.getRecyclerView();
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager manager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(manager);
            DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                    manager.getOrientation());
            Drawable drawable = this.getContext().getResources().getDrawable(R.drawable.form_step_divider);
            decoration.setDrawable(drawable);
            recyclerView.addItemDecoration(decoration);

            List<InputFieldView> inputFields = stepView.getInputFields();
            if (inputFields.isEmpty()) {
                LOGGER.warn("Form step with no input fields created.");
                return result;
            } else if (inputFields.size() > 1) {
                LOGGER.warn("Form step with more than 1 input field created, using the first input field.");
            }

            InputFieldView inputField = inputFields.get(0);
            if (!(inputField instanceof ChoiceInputFieldViewBase<?>)) {
                LOGGER.warn("Form step with a non ChoiceInput field created.");
                return result;
            }

            ChoiceInputFieldViewBase<?> choiceInputField = (ChoiceInputFieldViewBase<?>)inputField;
            FormUIAdapter adapter = new FormUIAdapter<>(choiceInputField.getChoices());
            recyclerView.setAdapter(adapter);
        }

        return result;
    }
}
