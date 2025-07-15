package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation

sealed interface HashtagPickerDialogNavigationEvent {
    data object OnNavigateBack : HashtagPickerDialogNavigationEvent
}