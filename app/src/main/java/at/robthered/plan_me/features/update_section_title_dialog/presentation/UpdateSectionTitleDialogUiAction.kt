package at.robthered.plan_me.features.update_section_title_dialog.presentation

sealed interface UpdateSectionTitleDialogUiAction {
    data object OnNavigateBack : UpdateSectionTitleDialogUiAction
    data object OnResetState : UpdateSectionTitleDialogUiAction
    data class OnChangeTitle(val title: String) : UpdateSectionTitleDialogUiAction
    data object OnUpdateSection : UpdateSectionTitleDialogUiAction
}