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

package org.sagebionetworks.research.mobile_ui.show_step.view.forms

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.sagebionetworks.research.domain.form.InputUIHint.CHECKBOX
import org.sagebionetworks.research.domain.form.InputUIHint.LIST
import org.sagebionetworks.research.domain.form.InputUIHint.RADIO_BUTTON
import org.sagebionetworks.research.domain.form.interfaces.Choice
import org.sagebionetworks.research.domain.form.interfaces.InputField
import org.sagebionetworks.research.mobile_ui.R
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormDataAdapter.ViewHolder
import org.sagebionetworks.research.mobile_ui.utils.getDrawableResourceId
import org.sagebionetworks.research.presentation.model.form.ChoiceView
import org.sagebionetworks.research.presentation.model.form.InputFieldView
import org.slf4j.LoggerFactory

/**
 * [ChoiceAdapterItem] is the base implementation of a [FormAdapterItem] that can display a choice
 * that can be selected and unselected.
 * @property choice choice for a single or multiple choice input field.
 */
open class ChoiceAdapterItem(
        inputField: InputFieldView<*>, uiHint: String, val choice: ChoiceView<*>,
        identifier: String, rowIndex: Int, sectionIdentifier: String? = null, itemViewType: Int? = null):
        InputFieldAdapterItem(inputField, uiHint, identifier, itemViewType, rowIndex, sectionIdentifier) {

    /**
     * @property listener for changes in the selected state.
     */
    var listener: OnSelectionChangedListener? = null

    /**
     * @property selected Whether or not the choice is currently selected.
     */
    var selected = false

    /**
     * @property answer The answer associated with this choice.
     */
    override val answer: Any? get() {
        if (selected) {
            return choice.answerValue
        }
        return null
    }

    override fun createViewHolder(parent: ViewGroup): ChoiceListItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (uiHint) {
            LIST -> createChoiceListViewHolder(layoutInflater, parent)
            // TODO: mdephillips 12/4/18 do checkbox and radio buttons
//            CHECKBOX -> createChoiceCheckBoxViewHolder(layoutInflater, parent)
//            RADIO_BUTTON -> createChoiceRadioButtonViewHolder(layoutInflater, parent)
            else -> createChoiceUnknownViewHolder(layoutInflater, parent)
        }
    }

    open protected fun createChoiceListViewHolder(
            layoutInflater: LayoutInflater, parent: ViewGroup): ChoiceListItemViewHolder {
        val itemView = layoutInflater.inflate(
                R.layout.rs2_adapter_item_choice_list, parent, false)
        return ChoiceListItemViewHolder(this, itemView)
    }

    open protected fun createChoiceCheckBoxViewHolder(
            layoutInflater: LayoutInflater, parent: ViewGroup): ChoiceListItemViewHolder {
        val itemView = layoutInflater.inflate(
                R.layout.rs2_adapter_item_choice_check_box, parent, false)
        return ChoiceListItemViewHolder(this, itemView)
    }

    open protected fun createChoiceRadioButtonViewHolder(
            layoutInflater: LayoutInflater, parent: ViewGroup): ChoiceListItemViewHolder {
        val itemView = layoutInflater.inflate(
                R.layout.rs2_adapter_item_choice_radio_button, parent, false)
        return ChoiceListItemViewHolder(this, itemView)
    }

    /**
     * @param layoutInflater to create the [ViewHolder]
     * @param viewGroup parent that the returned [ViewHolder] needs to be added to.
     * @return a [ViewHolder] that is ready to be added to the [RecyclerView] for this item.
     */
    open protected fun createChoiceUnknownViewHolder(
            layoutInflater: LayoutInflater, parent: ViewGroup): ChoiceListItemViewHolder {
        FormDataAdapter.logger.warn(
                "Did not recognize uiHint $uiHint when creating ViewHolder.  " +
                "Returning a list type by default.")
        return createChoiceListViewHolder(layoutInflater, parent)
    }

    interface OnSelectionChangedListener {
        /**
         * This function is called when the selection state changes.
         * @param item that has changed
         */
        fun selectionChanged(item: ChoiceAdapterItem)
    }
}

/**
 * [ChoiceListItemViewHolder] displays a selectable row that may have an icon, title, and subtitle,
 * if they are provided by a [ChoiceAdapterItem]
 */
open class ChoiceListItemViewHolder(item: ChoiceAdapterItem, itemView: View):
        ViewHolder(item, itemView) {

    private val logger = LoggerFactory.getLogger(ChoiceListItemViewHolder::class.java)

    val root = itemView.findViewById<View>(R.id.choice_list_root)
    val title = itemView.findViewById<TextView>(R.id.choice_list_title)
    val subtitle = itemView.findViewById<TextView>(R.id.choice_list_subtitle)
    val icon = itemView.findViewById<ImageView>(R.id.choice_list_icon)

    private val selectedBackgroundColor = ResourcesCompat.getColor(
        itemView.resources, R.color.choice_list_selected_background, null)
    private val unselectedBackgroundColor = ResourcesCompat.getColor(
            itemView.resources, R.color.choice_list_unselected_background, null)

    private val selectedTextColor = ResourcesCompat.getColor(
            itemView.resources, R.color.choice_list_selected_text, null)
    private val unselectedTextColor = ResourcesCompat.getColor(
            itemView.resources, R.color.choice_list_unselected_text, null)

    override fun bindViewHolder(item: FormAdapterItem) {
        this.item = item

        if (item !is ChoiceAdapterItem) {
            logger.error("Item must be ChoiceAdapterItem when used with ChoiceListItemViewHolder")
            return
        }

        item.choice.text?.getString(root.resources)?.let {
            title.text = it
            title.visibility = View.VISIBLE
        } ?: run {
            title.visibility = View.GONE
        }
        item.choice.detail?.getString(root.resources)?.let {
            subtitle.text = it
            subtitle.visibility = View.VISIBLE
        } ?: run {
            subtitle.visibility = View.GONE
        }

        if (item.selected) {
            title.setTextColor(selectedTextColor)
            subtitle.setTextColor(selectedTextColor)
            root.setBackgroundColor(selectedBackgroundColor)
        } else {
            title.setTextColor(unselectedTextColor)
            subtitle.setTextColor(unselectedTextColor)
            root.setBackgroundColor(unselectedBackgroundColor)
        }

        root.setOnClickListener {
            item.listener?.selectionChanged(item)
        }

        if (item.choice.iconResId != 0) {
            icon.visibility = View.VISIBLE
            icon.contentDescription = item.choice.text?.getString(root.resources)
            icon.setImageResource(item.choice.iconResId)
        } else {
            icon.visibility = View.GONE
        }
    }
}