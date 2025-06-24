package at.robthered.plan_me.features.add_task_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs

sealed interface AddTaskDialogNavigationEvent {
    data object OnNavigateBack : AddTaskDialogNavigationEvent
    data object OnNavigateToPriorityPickerDialog : AddTaskDialogNavigationEvent
    data object OnNavigateToHashtagPickerDialog : AddTaskDialogNavigationEvent
    data class OnNavigateToTaskSchedulePickerDialog(val args: TaskSchedulePickerDialogArgs) :
        AddTaskDialogNavigationEvent
}