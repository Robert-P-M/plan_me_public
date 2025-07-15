package at.robthered.plan_me.features.update_section_title_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel

interface UpdateSectionTitleModelChangeChecker {
    operator fun invoke(
        initialModel: UpdateSectionTitleModel,
        currentModel: UpdateSectionTitleModel,
    ): Boolean
}