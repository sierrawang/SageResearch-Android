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

import androidx.recyclerview.widget.RecyclerView
import org.sagebionetworks.research.domain.form.interfaces.InputField
import java.util.UUID

/**
 * [FormAdapterItemGroup] is a generic adapter item group object that can be used to display information in a [RecyclerView]
 * that does not have an associated [InputField].
 */
abstract class FormAdapterItemGroup<T: FormAdapterItem>(
        /**
         * @property items The list of items (or rows) included in this group.
         *                 An adapter group can be used to represent one or more rows.
         */
        val items: List<T>,
        /**
         * @property info about this []FormAdapterItemGroup].
         */
        var info: Info
) {
    /**
     * @return If the current answer is valid.
     *         Also checks the case where answer is required but one has not been provided.
     */
    abstract fun isAnswerValid(): Boolean

    var sectionIndex: Int
        set(newValue) {
            info.sectionIndex = newValue
        }
        get() {
            return info.sectionIndex
        }

    val beginningRowIndex: Int get() {
        return info.beginningRowIndex
    }

    val uuid: UUID get() {
        return info.uuid
    }

    /**
     * Helper data class to control constructor parameter length.
     */
    data class Info(
            /**
             * @property sectionIndex The section index for this group.
             */
            var sectionIndex: Int = 0,
            /**
             * @property beginningRowIndex The row index for the first row in the group.
             */
            val beginningRowIndex: Int = 0,
            /**
             * @property uuid A unique identifier that can be used to track the group.
             */
            val uuid: UUID = UUID.randomUUID())
}

/**
 * [FormAdapterItemGroupBase] base class that always returns true for it's answers being valid.
 */
open class FormAdapterItemGroupBase(items: List<FormAdapterItem>, info: Info):
        FormAdapterItemGroup<FormAdapterItem>(items, info) {
    override fun isAnswerValid(): Boolean {
        return true
    }
}