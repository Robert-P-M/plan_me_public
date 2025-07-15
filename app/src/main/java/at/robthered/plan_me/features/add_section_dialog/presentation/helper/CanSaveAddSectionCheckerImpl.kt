package at.robthered.plan_me.features.add_section_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError

class CanSaveAddSectionCheckerImpl : CanSaveAddSectionChecker {
    override operator fun invoke(addSectionModelError: AddSectionModelError): Boolean {
        return addSectionModelError.title == null
    }
}