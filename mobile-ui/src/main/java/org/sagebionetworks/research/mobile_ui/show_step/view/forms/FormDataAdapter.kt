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

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.sagebionetworks.research.domain.form.InputUIHint
import org.sagebionetworks.research.domain.form.interfaces.InputField
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase
import org.sagebionetworks.research.domain.result.implementations.CollectionResultBase
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult
import org.sagebionetworks.research.domain.result.interfaces.CollectionResult
import org.sagebionetworks.research.domain.result.interfaces.Result
import org.sagebionetworks.research.domain.step.interfaces.Step
import org.sagebionetworks.research.mobile_ui.show_step.view.FormUIStepFragment
import org.sagebionetworks.research.mobile_ui.show_step.view.forms.FormDataAdapter.ViewHolder
import org.sagebionetworks.research.presentation.model.form.ChoiceInputFieldViewBase
import org.sagebionetworks.research.presentation.model.form.InputFieldView
import org.sagebionetworks.research.presentation.model.interfaces.FormUIStepView
import org.sagebionetworks.research.presentation.model.interfaces.StepView
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView
import org.slf4j.LoggerFactory
import org.threeten.bp.Instant

/**
 * Based on iOS' RSDTableDataSource, [FormDataAdapter] is the model for [FormUIStepFragment]'s RecyclerView.
 * It provides the [RecyclerView.Adapter], manages and stores answers provided through user input,
 * and provides a [Result] with those answers upon request.
 *
 * It also provides several convenience methods for saving or selecting answers, checking if all answers
 * are valid, and retrieving specific model objects that may be needed by the Fragment.
 *
 * The [FormDataAdapter] is comprised of 3 objects:
 *
 * 1. [FormAdapterSection]: An object representing a section in the RecyclerView. It has one or more
 *    [DataSourceItem] objects.
 *
 * 2. [FormAdapterItem]: An object representing a specific [RecyclerView.ViewHolder] cell.
 *    There will be one [DataSourceItem] for each (section, item) combo in the RecyclerView.
 *
 * 3. [FormAdapterItemGroup]: An object representing a specific question supplied by [Step] as an [InputField].
 *     Upon init(), the FormAdapterItemGroup will create one or more [DataSourceItem] objects representing the
 *     answer options for the [InputField]. The FormAdapterItemGroup is responsible for storing/computing the
 *     answers for its [InputField].
 */
open class FormDataAdapter(
        /**
         * @param resources Used only in initialization.
         */
        resources: Resources,
        /**
         * @property step the step driving the creation of the sections and groups.
         */
        val stepView: UIStepView,
        /**
         * @property initialResult The initial result when the data source adapter was first displayed.
         */
        var collectionResult: CollectionResult =
                CollectionResultBase(stepView.identifier, Instant.now(), Instant.now(), listOf()),

        /**
         * [SectionBuilderDelegate] allows for composition changes without sub-classing.
         *                          without initializer sub-class execution order bugs.
         */
        sectionBuilder: SectionBuilderDelegate = SectionBuilderBase()):

        RecyclerView.Adapter<ViewHolder>() {

    companion object {
        val logger = LoggerFactory.getLogger(FormDataAdapter::class.java)
    }

    /**
     * @property listener  The listener associated with this data source.
     */
    var listener: Listener? = null

    /**
     * @property sections the sections displayed in this table.
     */
    var sections: List<FormAdapterSection> = listOf()

    /**
     * @property itemGroups the item groups displayed in this table.
     */
    var itemGroups: List<FormAdapterItemGroup<*>> = listOf()

    init {
        // Populate the sections and initial results.
        val builderResult =
                sectionBuilder.buildSections(resources, stepView, collectionResult)
        sections = builderResult.sections
        itemGroups = builderResult.itemGroups
        populateInitialResults()
    }

    /**
     * This function is used by [RecyclerView] to create a [RecyclerView.ViewHolder]
     * associated with the specified recyclerViewIndex.
     * This will always be called once to create the pool of [RecyclerView.ViewHolder] that
     * the [RecyclerView] will re-use with [onBindViewHolder] until it needs more, and
     * then this function may be called again to add as many to the pool as it needs.
     * @param parent [ViewGroup] of the the [RecyclerView.ViewHolder] that is returned.
     * @param itemViewType used to create the corresponding [ViewHolder]
     * @return a [ViewHolder] that is associated with the itemViewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): ViewHolder {
        firstItem(itemViewType)?.let {
            return it.createViewHolder(parent)
        }
        throw IllegalArgumentException("Cannot find form item for itemViewType $itemViewType")
    }

    /**
     * This function is used by [RecyclerView] to fill the [RecyclerView.ViewHolder]
     * with the current state of its contents, which is in our case, the corresponding [Item].
     * The [RecyclerView] re-uses viewHolderItems so make sure reset the UI completely,
     * or the UI of for some rows will copy over into other rows.
     * @param viewHolderItem to bind.
     * @param recyclerViewIndex associated with this [ViewHolder]
     */
    override fun onBindViewHolder(viewHolderItem: ViewHolder, recyclerViewIndex: Int) {
        val indexPath = indexPath(recyclerViewIndex) ?: run {
            return
        }
        val item = item(indexPath) ?: run {
            return
        }
        viewHolderItem.bindViewHolder(item)
        bindAdapterItemListeners(item, indexPath)
    }

    /**
     * This function is called after this item is bound and we can register for any listener callbacks
     * that the view holders may fire off.
     * @param item that was just bound through the [RecyclerView]
     * @param indexPath the index path of the item
     */
    open fun bindAdapterItemListeners(item: FormAdapterItem, indexPath: IndexPath) {
        (item as? ChoiceAdapterItem)?.let {
            it.listener = object : ChoiceAdapterItem.OnSelectionChangedListener {
                override fun selectionChanged(item: ChoiceAdapterItem) {
                    if (selectAnswer(it, indexPath).reloadSection) {
                        notifyItemGroupChanged(indexPath)
                    }
                }
            }
        }
    }

    /**
     * Notifies the adapter that the range of items in the item group has changed
     * so that [RecyclerView.Adapter.onBindViewHolder] can be called on the [ViewHolder]'s again
     * @param indexPath within the item group to find
     */
    open fun notifyItemGroupChanged(indexPath: IndexPath) {
        itemGroup(indexPath)?.let {
            notifyItemRangeChanged(it.beginningRowIndex, it.items.size)
        }
    }

    /**
     * This function is used by the [RecyclerView] to determine how many [RecyclerView.ViewHolder]'s to create.
     * @return the total items in the sections.
     */
    override fun getItemCount(): Int {
        return sections.sumBy { it.rowCount }
    }

    /**
     * This function provides an Int that maps to a specific ViewHolder to create.
     */
    override fun getItemViewType(recyclerViewIndex: Int): Int {
        return item(recyclerViewIndex)?.itemViewType ?: 0
    }

    open fun item(recyclerViewIndex: Int): FormAdapterItem? {
        indexPath(recyclerViewIndex)?.let {
            return item(it)
        }
        logger.warn("Could not find item at recyclerViewIndex $recyclerViewIndex")
        return null
    }

    open fun item(indexPath: IndexPath): FormAdapterItem? {
        // Protect against IndexOutOfBoundExceptions
        if (sections.isNotEmpty() &&
                indexPath.sectionIndex < sections.size &&
                indexPath.rowIndex < sections[indexPath.sectionIndex].rowCount) {
            return sections[indexPath.sectionIndex].items[indexPath.rowIndex]
        }
        logger.warn("indexPath $indexPath was found to be out of bounds for sections")
        return null
    }

    /**
     * @param itemViewType of the item to find.
     * @return the first item that has the corresponding view type.
     */
    open fun firstItem(itemViewType: Int): FormAdapterItem? {
        sections.flatMap { it.items }.firstOrNull {
            it.itemViewType == itemViewType
        }?.let { return it }
        logger.warn("Could not find item with itemViewType $itemViewType " +
                "it is probably the hashCode of the item\'s identifier")
        return null
    }

    /**
     * RecyclerView works with linear indexes by default.
     * The DataSourceAdapter works with sections and rows by default.
     * We must be able to convert between the two concepts to make the class work.
     * This function is for converting from a RecyclerView concept of index, to our IndexPath concept.
     * @param recyclerViewIndex how the RecyclerView treats linear indexes.
     * @return the section and row indexes from the linear index.
     */
    open fun indexPath(recyclerViewIndex: Int): IndexPath? {
        var indexSum = 0
        sections.forEachIndexed { index, section ->
            val newIndexSum = indexSum + section.rowCount
            if (newIndexSum > recyclerViewIndex) {
                return IndexPath(index, recyclerViewIndex - indexSum)
            }
            indexSum = newIndexSum
        }
        logger.warn("Could not create indexPath based on recyclerViewIndex $recyclerViewIndex")
        return null
    }

    /**
     * RecyclerView works with linear indexes by default.
     * The DataSourceAdapter works with sections and rows by default.
     * We must be able to convert between the two concepts to make the class work.
     * This function is for converting from our IndexPath concept to the RecyclerView concept of index.
     * @param indexPath how our class treats indexes with sections and rows.
     * @return the correct index to feed into any base []RecyclerView] index values.
     */
    open fun recyclerViewIndex(indexPath: IndexPath): Int {
        var recyclerViewIndex = 0
        for (i in 0 until indexPath.sectionIndex) {
            if (i >= sections.size) {
                logger.warn("indexPath section $i is out of bounds for sections\' size ${sections.size}")
                return 0
            }
            recyclerViewIndex += sections[i].rowCount
        }
        return recyclerViewIndex + indexPath.rowIndex
    }

    /**
     * Instantiate the appropriate answer result for the given item group.
     * @param itemGroup The item group for which to create a result.
     * @return The answer result (if any).
     */
    open fun instantiateAnswerResult(itemGroup: InputFieldItemGroup<*>): AnswerResult<*>? {
        itemGroup.answer?.let {
            return AnswerResultBase(itemGroup.identifier, Instant.now(), Instant.now(), it, itemGroup.answerType)
        }
        return null
    }

    /**
     * Retrieve the [FormAdapterItemGroup] for a specific section index.
     * @param sectionIndex for the group in the adapter.
     * @return the requested [FormAdapterItemGroup], or null if it cannot be found.
     */
    open fun itemGroup(indexPath: IndexPath): FormAdapterItemGroup<*>? {
        return itemGroups.firstOrNull {
            isMatching(it, indexPath)
        }
    }

    private fun isMatching(itemGroup: FormAdapterItemGroup<*>, indexPath: IndexPath): Boolean {
        return itemGroup.sectionIndex == indexPath.sectionIndex &&
                indexPath.rowIndex >= itemGroup.beginningRowIndex &&
                indexPath.rowIndex < (itemGroup.beginningRowIndex + itemGroup.items.size)
    }

    /**
     * Retrieve the [FormAdapterItemGroup] with a specific [InputField] identifier.
     * @param inputFieldIdentifier The identifier of the [InputField] assigned to the item group.
     * @return The requested [FormAdapterItemGroup], or nil if it cannot be found.
     */
    open fun itemGroup(inputFieldIdentifier: String): FormAdapterItemGroup<*>? {
        return itemGroups.firstOrNull {
            inputFieldIdentifier == (it as? InputFieldItemGroup)?.inputField?.getIdentifier()
        }
    }

    /**
     * Determine if all answers are valid.
     * Also checks the case where answers are required but one has not been provided.
     * @return a [Boolean] indicating if all answers are valid.
     */
    open fun allAnswersValid(): Boolean {
        return itemGroups.fold(true) { acc, it -> acc && it.isAnswerValid() }
    }

    /**
     * Save an answer for a specific sectionIndex and rowIndex.
     * @param answer the object to be saved as the answer.
     * @param indexPath the represents the [FormAdapterItem] in the adapter.
     */
    open fun saveAnswer(answer: Any, indexPath: IndexPath) {
        val itemGroup = (itemGroup(indexPath) as? InputFieldItemGroup) ?: run {
            logger.error("Could not find item group at indexPath $indexPath to save answer.")
            return
        }
        // TODO: mdephillips 12/2/18 address this when TextAdapterItem is added
        // RSDTextInputTableItem has different set answer with try clause, we may not need this on Android
//        let newAnswer = (answer is NSNull) ? nil : answer
//        if let tableItem = self.tableItem(at: indexPath) as? RSDTextInputTableItem {
//            // If this is a text input table item then store the answer on the table item instead of on the group.
//            try tableItem.setAnswer(newAnswer)
//            } else {
//            try itemGroup.setAnswer(newAnswer)
//            }
//        _answerDidChange(for: itemGroup, at: indexPath)
        itemGroup.setAnswer(answer)
        answerDidChange(itemGroup)
    }

    private fun answerDidChange(itemGroup: InputFieldItemGroup<*>) {
        // Update the answers
        (instantiateAnswerResult(itemGroup))?.let {
            collectionResult = collectionResult.appendInputResult(it)
        } ?: run {
            collectionResult = collectionResult.removeInputResult(itemGroup.identifier)
        }
        // inform listener that answers have changed
        listener?.didChangeAnswer(itemGroup)
    }

    /**
     * Select or deselect the answer option for a specific sectionIndex and rowIndex.
     * @param item The adapter item that was selected or deselected.
     * @param indexPath that represents the [Item] in the adapter.
     * @return a pair of booleans for if the item is now selected and if it needs reloaded visually.
     */
    open fun selectAnswer(item: FormAdapterItem, indexPath: IndexPath): SelectReturnValue {
        (item as? ChoiceAdapterItem)?.let { choiceItem ->
            (itemGroup(indexPath) as? ChoiceItemGroup)?.let { choiceGroup ->
                val selectReturnValue = choiceGroup.select(choiceItem, indexPath)
                answerDidChange(choiceGroup)
                return selectReturnValue
            }
        }
        return SelectReturnValue(false, false)
    }

    /**
     * Convenience method for looking at the previous results in the task path and setting the answer
     * based on that result. Get the collection result for this step and populate that result with the
     * initial results that are valid from this form.
     *
     * - note: This is **not** handled universally by the [PerformTaskViewModel] for all steps because it
     * is possible that a different implementation should not include populating the current result with
     * a previous result. For example, a form should be populated with previous answers, but an active
     * test should not.
     */
    private fun populateInitialResults() {
        var itemGroupChanged: FormAdapterItemGroup<*>? = null

        val appendResults: (it: InputFieldItemGroup<*>) -> Unit = {
            instantiateAnswerResult(it)?.let { answerResult ->
                collectionResult = collectionResult.appendInputResult(answerResult as Result)
                itemGroupChanged = it
            }
        }

        if (collectionResult.inputResults.isNotEmpty()) {
            collectionResult.inputResults.forEach { result ->
                (itemGroup(result.identifier) as? InputFieldItemGroup)?.let {
                    it.setAnswer(result)
                    appendResults(it)
                }
            }
        }

        itemGroups.forEach { formGroup ->
            (formGroup as? InputFieldItemGroup)?.let {
                if (it.setDefaultAnswerIfValid()) {
                    appendResults(it)
                }
            }
        }

        itemGroupChanged?.let {
            listener?.didChangeAnswer(it)
        }
    }

    /**
     * [ViewHolder] is the base class to be used with [FormDataAdapter].
     */
    abstract class ViewHolder(
            /**
             * @property item that this [ViewHolder] represents.
             */
            var item: FormAdapterItem,
            /**
             * The View for this item, it is used to construct the base [RecyclerView.ViewHolder].
             */
            itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        /**
         * This is a pass through function from [RecyclerView.Adapter.bindViewHolder]
         * @param item associated with the content of this [ViewHolder]
         */
        abstract fun bindViewHolder(item: FormAdapterItem)
    }

    /**
     * [DataSourceAdapter.Listener] handles callbacks from the adapter to the owner.
     */
    interface Listener {
        /**
         * Called when the answers tracked by the data source change.
         * @param itemGroup That had its answer changed.
         */
        fun didChangeAnswer(itemGroup: FormAdapterItemGroup<*>)
    }

    data class IndexPath(
        /**
         * @property sectionIndex the [FormAdapterSection] index for the adapter.
         */
        val sectionIndex: Int,
        /**
         * @property rowIndex the [Item] index within the [FormAdapterSection] for the adapter.
         */
        val rowIndex: Int)
}

interface SectionBuilderDelegate {
    fun buildSections(resources: Resources, stepView: StepView, initialResult: Result?): SectionsInitializerReturn
    fun instantiateItemGroup(inputField: InputFieldView<*>, beginningRowIndex: Int): FormAdapterItemGroup<*>
}

open class SectionBuilderBase: SectionBuilderDelegate {
    override fun buildSections(
            resources: Resources, stepView: StepView, initialResult: Result?): SectionsInitializerReturn {

        if (stepView !is UIStepView) {
            FormDataAdapter.logger.error("FormStepSectionsInitializer only works with FormUISteps")
            return SectionsInitializerReturn(
                    emptyList(), emptyList())
        }
        val sectionBuilders = mutableListOf<SectionBuilder>()
        ((stepView as? FormUIStepView)?.inputFields ?: emptyList<InputFieldView<*>>()).forEach { inputField ->
            val lastSectionBuilder = sectionBuilders.lastOrNull()
            // Get the next row index
            var rowIndex = 0
            lastSectionBuilder?.let {
                if (!it.singleFormItem) {
                    rowIndex = it.section.rowCount
                }
            }

            // Call open function to get the appropriate item group.
            val itemGroup = instantiateItemGroup(inputField, rowIndex)
            val needExclusiveSection = (itemGroup as? InputFieldItemGroup)?.requiresExclusiveSection ?: false

            // If we don't need an exclusive section and we have an existing section and it's not exclusive
            // ('singleFormItem'), then add this item to that existing section, otherwise create a new one.
            if (!needExclusiveSection && (lastSectionBuilder != null && !lastSectionBuilder.singleFormItem)) {
                lastSectionBuilder.appendGroup(itemGroup)
            } else {
                val section = SectionBuilder(mutableListOf(itemGroup), sectionBuilders.size, needExclusiveSection)
                (itemGroup as? ChoiceItemGroup)?.let {
                    if (it.items.size > 1) {
                        section.title = it.inputField.prompt?.getString(resources)
                        section.subtitle = it.inputField.promptDetail?.getString(resources)
                    }
                }
                sectionBuilders.add(section)
            }
        }

        var sections = sectionBuilders.map { it.section }
        val itemGroups = sectionBuilders.map { it.itemGroups }.flatMap { it }

        // TODO: mdephillips 12/1/18 add these in once we create ImageAdapterItem and TextAdapterItem
//      // add image below and footnote
//      var items: [RSDTableItem] = []
//      if let imageTheme = (step as? RSDThemedUIStep)?.imageTheme, imageTheme.placementType == .iconAfter {
//          items.append(RSDImageTableItem(rowIndex: items.count, imageTheme: imageTheme))
//      }
//      if let footnote = uiStep.footnote {
//          items.append(RSDTextTableItem(rowIndex: items.count, text: footnote))
//      }
//      if items.count > 0 {
//          let sectionIndex = sections.count
//                  let section = RSDTableSection(identifier: "\(sectionIndex)", sectionIndex: sectionIndex, tableItems: items)
//          sections.append(section)
//      }

        return SectionsInitializerReturn(sections, itemGroups)
    }

    /**
     * Instantiate the appropriate item group for this input field.
     * @param inputField The input field to convert to an item group.
     * @param beginningRowIndex The beginning row index for this item group.
     * @return The instantiated item group.
     */
    override fun instantiateItemGroup(inputField: InputFieldView<*>, beginningRowIndex: Int): FormAdapterItemGroup<*> {
        val uiHint = preferredUIHint(inputField)
        (inputField as? ChoiceInputFieldViewBase<*>)?.let {
            return ChoiceItemGroup.create(beginningRowIndex, it, uiHint)
        }
        // TODO: mdephillips 12/1/18 add in the other types
        //        let uiHint = preferredUIHint(for: inputField)
//        if case .measurement(_,_) = inputField.dataType {
//            return RSDHumanMeasurementTableItemGroup(beginningRowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint)
//        }
//        else if let pickerSource = inputField.pickerSource as? RSDChoiceOptions {
//            return RSDChoicePickerTableItemGroup(beginningRowIndex: 0, inputField: inputField, uiHint: uiHint, choicePicker: pickerSource)
//        }
//        else if let pickerSource = inputField.pickerSource as? RSDMultipleComponentPickerDataSource {
//            return RSDMultipleComponentTableItemGroup(beginningRowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint, pickerSource: pickerSource)
//        } else {
//            switch inputField.dataType.baseType {
//                case .boolean:
//                return RSDBooleanTableItemGroup(beginningRowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint)
//                case .string, .codable:
//                return RSDTextFieldTableItemGroup(beginningRowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint)
//                case .date:
//                return RSDDateTableItemGroup(beginningRowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint)
//                case .decimal, .integer, .year, .fraction, .duration:
//                return RSDNumberTableItemGroup(beginningRowIndex: beginningRowIndex, inputField: inputField, uiHint: uiHint)
//            }
//        }
        FormDataAdapter.logger.error("InputField ${inputField.getFormDataType()} not supported by FormDataAdapter yet.")
        return FormAdapterItemGroupBase(emptyList(), FormAdapterItemGroup.Info())
    }

    /**
     * What is the preferred ui hint for this input field that is supported by this adapter? By default,
     * this will look for the uiHint from the inputField to be included in the supported hints and if
     * not, will return the preferred ui hint for the data type.
     * @param inputField The inputField to check.
     * @return The ui hint to return, must be a String of type [InputUIHint].
     */
    open fun preferredUIHint(inputField: InputFieldView<*>): String {
        val uiHint = inputField.formUIHint
        uiHint?.let { return it }
        inputField.formDataType.validStandardUIHints().firstOrNull {
            // where:{ supportedHints.contains($0) }
            true  // eventually we should have supportedHints feature
        }?.let { return it }
        // TODO: mdephillips 12/1/2018 this code copied from iOS does is not supported yet
        //        if (uiHint != null && supportedHints.contains(uiHint)) {
//            return uiHint
//        }
//        if let choiceInput = inputField.pickerSource as? RSDChoiceOptions, choiceInput.hasImages {
//            standardType = supportedHints.contains(.slider) ? .slider : nil
//        } else {
//            standardType = inputField.dataType.validStandardUIHints.first(where:{ supportedHints.contains($0) })
//        }
//        return standardType ?? .textfield
        return InputUIHint.TEXTFIELD
    }
}

data class SectionsInitializerReturn(
        val sections: List<FormAdapterSection>,
        val itemGroups: List<FormAdapterItemGroup<*>>)

/**
 * [SectionBuilder] is data class used to make the [FormStepSectionsInitializer.buildSections]
 * function easier to understand.
 */
private data class SectionBuilder(
        var itemGroups: MutableList<FormAdapterItemGroup<*>> = mutableListOf(),
        val index: Int,
        val singleFormItem: Boolean,
        var title: String? = null,
        var subtitle: String? = null) {

    val section: FormAdapterSection
        get() {
            val items = itemGroups.map { it.items }.flatMap { it }
            return FormAdapterSection("$index",
                    items, index, title, subtitle)
        }

    fun appendGroup(itemGroup: FormAdapterItemGroup<*>) {
        itemGroup.sectionIndex = index
        itemGroups.add(itemGroup)
    }
}