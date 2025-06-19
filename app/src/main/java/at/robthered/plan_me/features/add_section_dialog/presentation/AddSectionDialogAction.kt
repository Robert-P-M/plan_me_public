package at.robthered.plan_me.features.add_section_dialog.presentation

sealed interface AddSectionDialogAction {
    data class OnChangeTitle(val title: String) : AddSectionDialogAction
    data object OnResetState : AddSectionDialogAction
    data object OnSaveSection : AddSectionDialogAction
    data object OnNavigateBack : AddSectionDialogAction
}