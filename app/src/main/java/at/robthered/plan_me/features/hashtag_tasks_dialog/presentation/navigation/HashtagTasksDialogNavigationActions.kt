package at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs


interface HashtagTasksDialogNavigationActions {
    fun onNavigateBack()
    fun onNavigateToTaskDetailsDialog(args: TaskDetailsDialogArgs)
    fun onNavigateToUpdateHashtagNameDialog(args: UpdateHashtagNameDialogArgs)
    fun onNavigateToUpdateTaskDialog(args: UpdateTaskDialogArgs)
}