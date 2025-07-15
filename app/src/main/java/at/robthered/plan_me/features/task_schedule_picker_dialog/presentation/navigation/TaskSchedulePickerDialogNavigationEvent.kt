package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation

sealed interface TaskSchedulePickerDialogNavigationEvent {
    data object OnNavigateBack : TaskSchedulePickerDialogNavigationEvent
}