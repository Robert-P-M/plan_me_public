package at.robthered.plan_me.features.update_section_title_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError

interface CanSaveUpdateSectionTitleChecker {
    operator fun invoke(updateSectionTitleModelError: UpdateSectionTitleModelError): Boolean
}