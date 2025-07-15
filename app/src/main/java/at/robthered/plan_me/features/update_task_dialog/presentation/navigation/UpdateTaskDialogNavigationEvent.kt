package at.robthered.plan_me.features.update_task_dialog.presentation.navigation

sealed interface UpdateTaskDialogNavigationEvent {
    data object OnNavigateBack : UpdateTaskDialogNavigationEvent
    data object OnNavigateToPriorityPickerDialog : UpdateTaskDialogNavigationEvent
}