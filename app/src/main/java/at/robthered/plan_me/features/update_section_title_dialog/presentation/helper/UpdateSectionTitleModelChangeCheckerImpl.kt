package at.robthered.plan_me.features.update_section_title_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel


class UpdateSectionTitleModelChangeCheckerImpl : UpdateSectionTitleModelChangeChecker {
    override operator fun invoke(
        initialModel: UpdateSectionTitleModel,
        currentModel: UpdateSectionTitleModel,
    ): Boolean {
        return initialModel.title != currentModel.title
    }
}