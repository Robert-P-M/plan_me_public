package at.robthered.plan_me.features.add_section_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel


class AddSectionModelChangeCheckerImpl : AddSectionModelChangeChecker {
    override operator fun invoke(
        initialModel: AddSectionModel,
        currentModel: AddSectionModel,
    ): Boolean {
        return initialModel.title != currentModel.title
    }
}