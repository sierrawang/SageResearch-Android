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

package org.sagebionetworks.research.mobile_ui.show_step.view

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ItemDecoration
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sagebionetworks.research.domain.result.interfaces.CollectionResult
import org.sagebionetworks.research.domain.result.interfaces.TaskResult
import org.sagebionetworks.research.mobile_ui.R
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormDataAdapter
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.FormUIStepViewBinding
import org.sagebionetworks.research.presentation.model.interfaces.FormUIStepView
import org.sagebionetworks.research.presentation.model.interfaces.StepView
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel
import org.sagebionetworks.research.domain.step.interfaces.FormUIStep
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormAdapterItem
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormAdapterItemGroup
import org.sagebionetworks.research.presentation.model.form.InputFieldView
import org.slf4j.LoggerFactory

/**
 * The [FormUIFragment] is the base class for displaying a [FormUIStepView] and its corresponding
 * [InputFieldView] within a [RecyclerView].  It uses a [FormDataAdapter] as the adapter for the recycler view.
 * Each of the different input fields will have their own [FormDataAdapter.ViewHolder] that
 * displays a different UI for the user to interact with to set an answer that is compiled into
 * a [CollectionResult] and added to the [TaskResult].
 */
open class FormUIStepFragment: ShowStepFragmentBase
    <FormUIStepView, ShowUIStepViewModel<FormUIStepView>, FormUIStepViewBinding<FormUIStepView>>() {

    companion object {
        private val logger = LoggerFactory.getLogger(FormUIStepFragment::class.java)

        fun newInstance(stepView: StepView): FormUIStepFragment {
            if (stepView !is FormUIStepView) {
                throw IllegalArgumentException("Step view: $stepView is not a FormUIStepView.")
            }
            val fragment = FormUIStepFragment()
            val arguments = ShowStepFragmentBase.createArguments(stepView)
            fragment.arguments = arguments
            return fragment
        }
    }

    /**
     * @return the initial result of the [FormDataAdapter].
     */
    protected open fun initialFormResult(): CollectionResult? {
        return performTaskViewModel.taskResult.stepHistory.firstOrNull {
            stepView.identifier == it.identifier
        } as? CollectionResult
    }

    /**
     * @property adapter that displays the [FormUIStep] in the [RecyclerView]
     */
    lateinit var adapter: FormDataAdapter
    protected open fun initializeAdapter() {
        initialFormResult()?.let {
            adapter = FormDataAdapter(resources, stepView, it)
        } ?: run {
            adapter = FormDataAdapter(resources, stepView)
        }
        adapter.listener = object : FormDataAdapter.Listener {
            override fun didChangeAnswer(itemGroup: FormAdapterItemGroup<*>) {
                // By appending the task result history each time an answer changes,
                // we make sure that state is not lost if this fragment is created/destroyed for any reason
                performTaskViewModel.addStepResult(adapter.collectionResult)
            }
        }
    }

    /**
     * @property [RecyclerView] layout manager.
     */
    lateinit var layoutManager: LayoutManager
    /**
     * Initialize the [RecyclerView]'s [LayoutManager].
     * This function is only called if the [StepView] has a non-null [RecyclerView]
     */
    protected open fun initializeLayoutManager() {
        layoutManager = LinearLayoutManager(context)
    }

    /**
     * Initialize an item decorator if you desire one for the [RecyclerView].
     * This function is only called if the [StepView] has a non-null [RecyclerView]
     * @return an ItemDecoration, or null if none is desired.
     */
    protected open fun initializeItemDecorator(): ItemDecoration? {
        (layoutManager as? LinearLayoutManager)?.let { linearLayoutManager ->
            val decoration = DividerItemDecoration(context, linearLayoutManager.orientation)
            ResourcesCompat.getDrawable(resources, R.drawable.form_step_divider, null)?.let {
                decoration.setDrawable(it)
                return decoration
            } ?: run {
                logger.warn("Failed to load form_step_divider drawable")
            }
        }
        return null
    }

    protected open val recyclerViewHasFixedSize: Boolean get() {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initializeRecyclerView()
        return view
    }

    override fun getLayoutId(): Int {
        return R.layout.rs2_form_step
    }

    override fun instantiateAndBindBinding(view: View): FormUIStepViewBinding<FormUIStepView> {
        return FormUIStepViewBinding(view)
    }

    /**
     * Initializes the recycler view, adapter, layout manager, and decorator.
     */
    protected open fun initializeRecyclerView() {
        initializeAdapter()
        stepViewBinding.recyclerView?.let { recyclerView ->
            recyclerView.setHasFixedSize(recyclerViewHasFixedSize)

            initializeLayoutManager()
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            initializeItemDecorator()?.let {
                recyclerView.addItemDecoration(it)
            }
        }
    }
}