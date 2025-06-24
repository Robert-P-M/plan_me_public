package at.robthered.plan_me.features.task_hashtags_dialog.presentation

import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

sealed interface TaskHashtagsDialogUiAction {
    data object OnNavigateBack : TaskHashtagsDialogUiAction
    data class OnDeleteTaskHashtagReference(val hashtagId: Long) : TaskHashtagsDialogUiAction
    data class OnNavigateToUpdateHashtagNameDialog(val args: UpdateHashtagNameDialogArgs) :
        TaskHashtagsDialogUiAction

    data class OnChangeHashtagName(val name: String) : TaskHashtagsDialogUiAction
    data object OnSaveNewHashtag : TaskHashtagsDialogUiAction
    data class OnSaveExistingHashtag(val existingHashtagModel: UiHashtagModel.ExistingHashtagModel) :
        TaskHashtagsDialogUiAction

    data object OnResetState : TaskHashtagsDialogUiAction

    data class OnNavigateToHashtagTasksDialog(val args: HashtagTasksDialogArgs) :
        TaskHashtagsDialogUiAction
}