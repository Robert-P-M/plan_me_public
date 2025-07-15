package at.robthered.plan_me.features.move_task_dialog.presentation.navigation

sealed interface MoveTaskDialogNavigationEvent {
    data object OnNavigateBack : MoveTaskDialogNavigationEvent
}