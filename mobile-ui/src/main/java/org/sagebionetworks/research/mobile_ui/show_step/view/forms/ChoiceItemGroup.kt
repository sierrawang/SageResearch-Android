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

import org.sagebionetworks.research.domain.form.data_types.CollectionInputDataType
import org.sagebionetworks.research.domain.form.data_types.CollectionInputDataType.CollectionType.SINGLE_CHOICE
import org.sagebionetworks.research.domain.form.implementations.ChoiceInputField
import org.sagebionetworks.research.domain.form.interfaces.InputField
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult
import org.sagebionetworks.research.domain.result.interfaces.Result
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormDataAdapter.IndexPath
import org.sagebionetworks.research.presentation.model.form.ChoiceInputFieldViewBase
import org.sagebionetworks.research.presentation.model.form.ChoiceView
import org.sagebionetworks.research.presentation.model.form.InputFieldView

/**
 * [ChoiceItemGroup] subclasses [ItemGroup] to implement a single or multiple
 * choice question where the choices are presented as a list.
 *
 * @param singleSelection Does the item group allow for multiple choices or is it single selection?
 */
open class ChoiceItemGroup<T: ChoiceAdapterItem>(
        inputField: InputFieldView<*>, fieldInfo: InputFieldItemGroup.FieldInfo,
        val singleSelection: Boolean, items: List<T>, groupInfo: FormAdapterItemGroup.Info):
            InputFieldItemGroup<T>(inputField, fieldInfo, items, groupInfo) {

    companion object {

        /**
         * Helper function to create a ChoiceItemGroup with a subset of parameters.
         */
        fun create(beginningRowIndex: Int, inputField: ChoiceInputFieldViewBase<*>,
                uiHint: String, answerType: String? = null): ChoiceItemGroup<ChoiceAdapterItem> {
            // Set the items
            var items: List<ChoiceAdapterItem> = mutableListOf()
            var singleSelection = true

            if (inputField.getFormDataType().listSelectionHints().contains(uiHint)) {
                (inputField.getFormDataType() as? CollectionInputDataType)?.let {
                    singleSelection = (SINGLE_CHOICE == it.collectionType)
                }
                items = inputField.getChoices().mapIndexed { index, choice ->
                    val rowIndex = beginningRowIndex + index
                    var identifier = "$rowIndex"
                    choice.answerValue?.let {
                        identifier = "${choice.answerValue}"
                    }
                    ChoiceAdapterItem(inputField, uiHint, choice, identifier, rowIndex)
                }
            }

            // Setup the answer type if nil
            val answerTypeUnwrapped = answerType ?: {
                // TODO: mdephillips 12/1/18 do we need this code yet?
//            let baseType: RSDAnswerResultType.BaseType = inputField.dataType.defaultAnswerResultBaseType()
//            let sequenceType: RSDAnswerResultType.SequenceType? = singleSelection ? nil : .array
//            let dateFormatter: DateFormatter? = (inputField.range as? RSDDateRange)?.dateCoder?.resultFormatter
//            let unit: String? = (inputField.range as? RSDNumberRange)?.unit
//            return RSDAnswerResultType(baseType: baseType, sequenceType: sequenceType, formDataType:inputField.dataType, dateFormat: dateFormatter?.dateFormat, unit: unit, sequenceSeparator: nil)
                inputField.getFormDataType().answerResultType
            }.invoke()

            val groupInfo = FormAdapterItemGroup.Info(beginningRowIndex = beginningRowIndex)
            val requiresExclusiveSection = (beginningRowIndex == 0 && items.isNotEmpty())
            val fieldInfo = InputFieldItemGroup.FieldInfo(uiHint, answerTypeUnwrapped, requiresExclusiveSection)
            return ChoiceItemGroup(inputField, fieldInfo, singleSelection, items, groupInfo)

//        // If this is being used as a picker source, then setup the picker
            // TODO: mdedphillips 12/1/18 add this when we create the text adapter item
//        if items == nil {
//            let item = RSDTextInputTableItem(rowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint, answerType: aType, textFieldOptions: nil, formatter: nil, pickerSource: choicePicker)
//            items = [item]
//        }
//
//        super.init(beginningRowIndex: beginningRowIndex, items: items!, inputField: inputField, uiHint: uiHint, answerType: aType)
        }
    }

    /**
     * Override to set the selected items from the result.
     */
    override fun setAnswer(result: Result) {
        super.setAnswer(result)
        // Set all the previously selected items as selected
        items.forEach {
            it.selected = isEqualToResult(it.choice, result)
        }
    }

    /**
     * @param choice to compare to the [Result]
     * @param result to compare to the [ChoiceView]
     * @return true if the choice answer value is equal to the result answer value, false if not.
     */
    protected open fun isEqualToResult(choice: ChoiceView<*>, result: Result): Boolean {
        (result as? AnswerResult<*>)?.let {
            return it.answer == choice.answerValue
        }
        return false
    }

    /**
     * Select or de-select an item (answer) at a specific indexPath.
     * This is used for text choice and boolean answers.
     * @param item the item to select.
     * @param indexPath the [IndexPath] of the item.
     * @return
     */
    open fun select(item: ChoiceAdapterItem, indexPath: IndexPath): SelectReturnValue {
        // To get the index of our item, add our `beginningRowIndex` to `indexPath.item`.
        val deselectOthers = singleSelection || item.choice.isExclusive ||
                items.firstOrNull { it.choice.isExclusive && it.selected } != null
        val index = indexPath.rowIndex - beginningRowIndex
        val selected = !item.selected

        // If we selected an item and this is a single-selection group, then we iterate
        // our other items and de-select them.
        val answers = mutableListOf<Any>()
        items.forEachIndexed { ii, it ->
            if (deselectOthers || (ii == index) || it.choice.isExclusive || it.choice.answerValue == null) {
                it.selected = (ii == index) && selected
            }
            if (it.selected && it.choice.answerValue != null) {
                answers.add(it.choice.answerValue)
            }
        }

        // Set the answer array
        if (singleSelection) {
            setAnswer(answers.firstOrNull())
        } else {
            setAnswer(answers)
        }

        return SelectReturnValue(selected, deselectOthers)
    }
}

data class SelectReturnValue(
        /**
         * @property isSelected The new selection state of the selected item.
         */
        val isSelected: Boolean,
        /**
         * @property reloadSection `true` if the section needs to be reloaded b/c other answers have changed,
         *                         otherwise returns `false`.
         */
        val reloadSection: Boolean)