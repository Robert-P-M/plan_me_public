package at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

sealed interface HashtagTasksDialogNavigationEvent {
    data object OnNavigateBack : HashtagTasksDialogNavigationEvent
    data class OnNavigateToTaskDetailsDialog(val args: TaskDetailsDialogArgs) :
        HashtagTasksDialogNavigationEvent

    data class OnNavigateToUpdateHashtagNameDialog(val args: UpdateHashtagNameDialogArgs) :
        HashtagTasksDialogNavigationEvent

    data class OnNavigateToUpdateTaskDialog(val args: UpdateTaskDialogArgs) :
        HashtagTasksDialogNavigationEvent
}