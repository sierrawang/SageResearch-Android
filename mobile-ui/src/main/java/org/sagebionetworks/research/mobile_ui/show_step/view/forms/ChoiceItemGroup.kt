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
import org.sagebionetworks.research.domain.form.interfaces.Choice
import org.sagebionetworks.research.domain.form.interfaces.InputField

/**
 * [ChoiceItemGroup] subclasses [ItemGroup] to implement a single or multiple
 * choice question where the choices are presented as a list.
 *
 * @param singleSelection Does the item group allow for multiple choices or is it single selection?
 */
open class ChoiceItemGroup<T: ChoiceAdapterItem>(
        inputField: InputField<*>, fieldInfo: InputFieldItemGroup.FieldInfo,
        val singleSelection: Boolean, items: List<T>, groupInfo: FormAdapterItemGroup.Info):
            InputFieldItemGroup<T>(inputField, fieldInfo, items, groupInfo) {

    companion object {

        /**
         * Helper function to create a ChoiceItemGroup with a subset of parameters.
         */
        fun create(beginningRowIndex: Int, inputField: ChoiceInputField<*>,
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
}