package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation

sealed interface UpdateHashtagNameDialogNavigationEvent {
    data object OnNavigateBack : UpdateHashtagNameDialogNavigationEvent
}