package at.robthered.plan_me.features.add_task_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs

interface AddTaskDialogNavigationActions {
    fun onNavigateBack()
    fun onNavigateToPriorityPickerDialog()
    fun onNavigateToHashtagPickerDialog()

    fun onNavigateToTaskSchedulePickerDialog(
        args: TaskSchedulePickerDialogArgs,
    )
}