package at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation

sealed interface UpdateSectionTitleDialogNavigationEvent {
    data object OnNavigateBack : UpdateSectionTitleDialogNavigationEvent
}