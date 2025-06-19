package at.robthered.plan_me.features.inbox_screen.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateSectionTitleDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

interface InboxScreenNavigationActions {
    fun onNavigateToAddTaskDialog(args: AddTaskDialogArgs)
    fun onNavigateToUpdateTaskDialog(args: UpdateTaskDialogArgs)
    fun onNavigateToAddSectionDialog()
    fun onNavigateToUpdateSectionTitleDialog(args: UpdateSectionTitleDialogArgs)
    fun onNavigateToPriorityPickerDialog()
    fun onNavigateToTaskDetailsDialog(args: TaskDetailsDialogArgs)
    fun onNavigateToTaskStatisticsDialog(args: TaskStatisticsDialogArgs)
    fun onNavigateToTaskHashtagsDialog(args: TaskHashtagsDialogArgs)
    fun onNavigateToMoveTaskDialog(args: MoveTaskDialogArgs)
}