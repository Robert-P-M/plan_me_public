package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation

sealed interface UpdateHashtagNameDialogUiAction {
    data object OnNavigateBack : UpdateHashtagNameDialogUiAction
    data class OnChangeName(val name: String) : UpdateHashtagNameDialogUiAction
    data object OnSaveHashtag : UpdateHashtagNameDialogUiAction
}