package at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs

sealed interface TaskHashtagsDialogNavigationEvent {
    data object OnNavigateBack : TaskHashtagsDialogNavigationEvent
    data class OnNavigateToHashtagTasksDialog(val args: HashtagTasksDialogArgs) :
        TaskHashtagsDialogNavigationEvent

    data class OnNavigateToUpdateHashtagNameDialog(val args: UpdateHashtagNameDialogArgs) :
        TaskHashtagsDialogNavigationEvent
}