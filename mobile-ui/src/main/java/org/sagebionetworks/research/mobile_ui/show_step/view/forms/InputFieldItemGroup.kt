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

import org.sagebionetworks.research.domain.result.interfaces.AnswerResult
import org.sagebionetworks.research.domain.result.interfaces.Result
import org.sagebionetworks.research.presentation.model.form.InputFieldView
import org.slf4j.LoggerFactory

/**
 * [InputFieldItemGroup] is used to represent a single input field.
 */
open class InputFieldItemGroup<T: InputFieldAdapterItem>(
        /**
         * @property inputField The input field associated with this item group.
         */
        val inputField: InputFieldView<*>,
        /**
         * @property fieldInfo about the [InputFieldItemGroup]
         */
        val fieldInfo: FieldInfo,
        /**
         * @param items that are a part of this group.
         */
        items: List<T>,
        /**
         * @param info about the [FormAdapterItemGroup] base class
         */
        info: FormAdapterItemGroup.Info):
            FormAdapterItemGroup<T>(items, info) {

    companion object {
        private val logger = LoggerFactory.getLogger(InputFieldItemGroup::class.java);
    }

    val identifier: String get() {
        return inputField.identifier
    }

    val uiHint: String get () {
        return fieldInfo.uiHint
    }

    val answerType: String get() {
        return fieldInfo.answerType
    }

    val requiresExclusiveSection: Boolean get() {
        return fieldInfo.requiresExclusiveSection
    }

    /**
     * @property answer The answer for this item group. This is the answer stored to the [AnswerResult].
     * The default implementation will return the privately stored answer if set and if not,
     * it will look to see if the first item is recognized as an [FormAdapterItem] that stores an answer on it.
     */
    open val answer: Any? get() {
        return _answer ?: null
            // TODO: mdephillips 12/1/18 add this line back in once text input adapter item is made
            // (items.firstOrNull() as? RSDTextInputTableItem)?.answer
    }
    private var _answer: Any? = null

    /**
     * Determine if the current answer is valid. Also checks the case where answer is required but one has
     * not been provided.
     * @return A [Boolean] indicating if answer is valid.
     */
    override fun isAnswerValid(): Boolean {
        // if answer is NOT optional and it equals Null, then it's invalid
        val isOptional = items.fold(inputField.isOptional()) { optional, item ->
            optional && item.inputField.isOptional()
        }
        return isOptional || answer != null
    }

    /**
     *
     */
    open fun setAnswer(newValue: Any?) {
        // Only validation at this level is on a single-input field. Otherwise, just set the answer and return.
        // TODO: mdephillips 12/1/18 add this line when we add text adapter items
//        if (items.size == 1) {
//            (items.firstOrNull() as? RSDTextInputTableItem)?.let {
//                try it.setAnswer(newValue)
//            }
//        }
        _answer = newValue
    }

    open fun setDefaultAnswerIfValid(): Boolean {
        // At this level, only the "date" has a default value.
        // TODO: mdephillips 12/1/18 integrate this code in after we make text adapter item and date picker data source
//        guard self.items.count == 1,
//        let textItem = self.items.first as? RSDTextInputTableItem,
//        textItem.answer == nil,
//        let defaultDate = (textItem.pickerSource as? RSDDatePickerDataSource)?.defaultDate
//          else {
//        return false }
//        do {
//            try textItem.setAnswer(defaultDate)
//                return true
//            }
//            catch let err {
//                debugPrint("Failed to set the default answer: \(err)")
//                return false
//            }
//        }
        return false
    }

    open fun setAnswer(result: Result) {
        if (result !is AnswerResult<*>) {
            logger.error("result must be of AnswerResult type")
            return
        }
        if (result.answerResultType != answerType) {
            logger.error("result answer type must match InputFieldItemGroup answerType")
            return
        }
        setAnswer(result.answer)
    }

    /**
     * Helper data class to control constructor parameter length
     */
    data class FieldInfo(
            /**
             * @property uiHint The UI hint for displaying the component of the item group.
             */
            val uiHint: String,
            /**
             * @property answerType The answer type for the input field result.
             */
            val answerType: String,
            /**
             * @property requiresExclusiveSection does this item group require an exclusive section?
             */
            val requiresExclusiveSection: Boolean)
}

