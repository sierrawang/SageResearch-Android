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

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sagebionetworks.research.domain.form.interfaces.InputField
import org.sagebionetworks.research.domain.form.InputUIHint
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormDataSourceAdapter.IndexPath
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormDataSourceAdapter.ViewHolder

/**
 * [FormAdapterItem] can be used to represent the type of the row to display.
 */
abstract class FormAdapterItem(
        /**
         * @property identifier A unique identifier for the item.
         *                      This identifier will be used to create the itemViewType of the [RecyclerView.ViewHolder].
         */
        val identifier: String,
        /**
         * @property rowIndex The index of this item relative to all rows in the section in which this item resides.
         */
        val rowIndex: Int,
        /**
         * @property identifier A unique identifier for the section this item belongs to.
         */
        val sectionIdentifier: String? = null,
        /**
         * @property sectionIndex The section index for this group.
         */
        val sectionIndex: Int = 0,
        /**
         * @property itemViewType for the [RecyclerView.Adapter.getItemViewType] function.
         */
        val itemViewType: Int) {

    /**
     * @param parent [ViewGroup] passed from [RecyclerView.Adapter.onCreateViewHolder]
     * @return the ViewHolder to be used for this [FormAdapterItem] in the [RecyclerView]
     */
    abstract fun createViewHolder(parent: ViewGroup): ViewHolder
}

/**
 * [InputFieldAdapterItem] is an abstract base class implementation for representing an answer, or part of an
 * answer for a given [InputField].
 */
abstract class InputFieldAdapterItem(
        /**
         * @property inputField The RSDInputField representing this tableItem.
         */
        val inputField: InputField<*>,
        /**
         * @property uiHint The UI hint for this row of the table. See [InputUIHint].
         */
        val uiHint: String, identifier: String,
        /**
         * @param itemViewType if null, a default one based on the uiHint will be provided.
         */
        itemViewType: Int? = null,
        rowIndex: Int,
        sectionIdentifier: String? = null):

        FormAdapterItem(identifier, rowIndex, sectionIdentifier,
                itemViewType = resolveItemViewType(itemViewType, uiHint)) {

    companion object {
        private fun resolveItemViewType(itemViewType: Int?, uiHint: String): Int {
            itemViewType?.let { return it }
            // If the itemViewType isn't passed to the constructor then set it from the ui hint.
            return uiHint.hashCode()
        }
    }

    /**
     * @property The answer associated with this adapter item component.
     */
    abstract val answer: Any?
}
