package at.robthered.plan_me.features.priority_picker_dialog.presentation

import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

sealed interface PriorityPickerDialogUiAction {
    data class OnPickPriority(val priorityEnum: PriorityEnum?) : PriorityPickerDialogUiAction
    data object OnNavigateBack : PriorityPickerDialogUiAction
}