package at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation

sealed interface PriorityPickerDialogNavigationEvent {
    data object OnNavigateBack : PriorityPickerDialogNavigationEvent
}