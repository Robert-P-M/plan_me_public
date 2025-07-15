package at.robthered.plan_me.features.add_section_dialog.presentation.navigation

sealed interface AddSectionDialogNavigationEvent {
    data object OnNavigateBack : AddSectionDialogNavigationEvent
}