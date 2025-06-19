package at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs

interface TaskHashtagsDialogNavigationActions {
    fun onNavigateBack()
    fun onNavigateToUpdateHashtagNameDialog(args: UpdateHashtagNameDialogArgs)
    fun onNavigateToHashtagTasksDialog(args: HashtagTasksDialogArgs)
}