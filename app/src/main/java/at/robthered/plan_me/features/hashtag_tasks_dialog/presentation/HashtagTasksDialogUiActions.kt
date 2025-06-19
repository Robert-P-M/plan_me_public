package at.robthered.plan_me.features.hashtag_tasks_dialog.presentation

import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

sealed interface HashtagTasksDialogUiActions {
    data class OnDeleteTaskHashtagReference(
        val taskId: Long,
    ) : HashtagTasksDialogUiActions

    data class OnNavigateToTaskDetailsDialog(
        val args: TaskDetailsDialogArgs,
    ) : HashtagTasksDialogUiActions

    data object OnNavigateBack : HashtagTasksDialogUiActions
    data class OnNavigateToUpdateHashtagNameDialog(
        val args: UpdateHashtagNameDialogArgs,
    ) : HashtagTasksDialogUiActions

    data class OnToggleCompleteTask(val taskId: Long, val title: String, val isCompleted: Boolean) :
        HashtagTasksDialogUiActions

    data class OnNavigateToUpdateTaskDialog(val args: UpdateTaskDialogArgs) :
        HashtagTasksDialogUiActions
}