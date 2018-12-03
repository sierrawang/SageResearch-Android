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

import org.sagebionetworks.research.domain.form.interfaces.Choice

/**
 * [PickerDataSource] includes information that can be used to build a choice UI element.
 * See iOS framework equivalent class RSDPickerDataSource.
 */
interface PickerDataSource {
    /**
     * @param selectedAnswer The answer to convert.
     * @return A text value for the answer to display to the user.
     */
    fun textAnswer(selectedAnswer: Any?): String?
}

/**
 *
 */
interface ChoicePickerDataSource<E>: PickerDataSource {

    /**
     * @property separator if this is a multiple component input field, the UI can optionally define a separator.
     * For example, blood pressure would have a separator of "/".
     */
    val separator: String?

    /**
     * @property defaultAnswer the default answer (if non-null) for this picker. If null, then the UI should display
     * empty rows initially, otherwise, the UI should display the default value.
     */
    val defaultAnswer: Any?

    /**
     * @property numberOfComponents Returns the number of 'columns' to display.
     */
    val numberOfComponents: Int

    /**
     * Returns the # of rows in each component.
     * @param The component (or column) of the picker.
     * @return The number of rows in the given component.
     */
    fun numberOfRows(component: Int): Int

    /**
     * Returns the choice for this row/component. If this is returns null, then this is the "skip" choice.
     * @param row The row for the selected component.
     * @param component The component (or column) of the picker.
     */
    fun choice(row: Int, component: Int): Choice<E>?

    /**
     * Returns the selected answer created by the union of the selected rows.
     * @param selectedRows The selected rows, where there is a selected row for each component.
     * @return The answer created from the given array of selected rows.
     */
    fun selectedAnswer(selectedRows: Array<Int>): Any?

    /**
     * Returns the selected rows that match the given selected answer (if non-null).
     * @param selectedAnswer The selected answer.
     * @return The selected rows, where there is a selected row for each component, or `null` if not
     *         all rows are selected.
     */
    fun selectedRows(selectedAnswer: Any?): Array<Int>?
}

/**
 * [ChoiceOptions] is a data source interface that can be used to set up a picker or list of choices.
 */
interface ChoiceOptionsPickerDataSource<E>: ChoicePickerDataSource<E> {
    /**
     * @property choices A list of choices for the input field.
     */
    val choices: Array<Choice<E>>

    /**
     * @property isOptional A Boolean value indicating whether the user can skip the input field without providing an answer.
     */
    val isOptional: Boolean
}