package at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs

interface TaskStatisticsDialogNavigationActions {
    fun onNavigateBack()
    fun onNavigateToTaskDetailsDialog(args: TaskDetailsDialogArgs)
    fun onNavigateToHashtagTasksDialog(args: HashtagTasksDialogArgs)
}