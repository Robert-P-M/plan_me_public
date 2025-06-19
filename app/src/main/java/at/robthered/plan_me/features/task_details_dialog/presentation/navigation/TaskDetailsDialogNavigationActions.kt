package at.robthered.plan_me.features.task_details_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

interface TaskDetailsDialogNavigationActions {
    fun onNavigateBack()
    fun onNavigateToTaskDetailsDialog(taskId: Long)
    fun onNavigateToUpdateTaskDialog(args: UpdateTaskDialogArgs)
    fun onNavigateToSectionDetails(sectionId: Long)
    fun onNavigateToAddTaskDialog(args: AddTaskDialogArgs)
    fun onNavigateToPriorityPickerDialog()
    fun onNavigateToTaskStatisticsDialog(args: TaskStatisticsDialogArgs)
    fun onNavigateToUpdateHashtagNameDialog(args: UpdateHashtagNameDialogArgs)
    fun onNavigateToTaskHashtagsDialog(args: TaskHashtagsDialogArgs)
    fun onNavigateToHashtagTasksDialog(args: HashtagTasksDialogArgs)
    fun onNavigateToMoveTaskDialog(args: MoveTaskDialogArgs)

    fun onNavigateToTaskScheduleEventDialog(args: TaskSchedulePickerDialogArgs)
}