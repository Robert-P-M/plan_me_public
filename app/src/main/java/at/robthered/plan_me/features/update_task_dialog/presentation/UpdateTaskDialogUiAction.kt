package at.robthered.plan_me.features.update_task_dialog.presentation

import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

sealed interface UpdateTaskDialogUiAction {
    data class OnChangeTitle(val title: String) : UpdateTaskDialogUiAction
    data class OnChangeDescription(val description: String?) : UpdateTaskDialogUiAction
    data class OnChangePriority(val priority: PriorityEnum?) : UpdateTaskDialogUiAction
    data object OnResetState : UpdateTaskDialogUiAction
    data object OnNavigateBack : UpdateTaskDialogUiAction
    data object OnNavigateToPriorityPickerDialog : UpdateTaskDialogUiAction
    data object OnUpdateTask : UpdateTaskDialogUiAction
}