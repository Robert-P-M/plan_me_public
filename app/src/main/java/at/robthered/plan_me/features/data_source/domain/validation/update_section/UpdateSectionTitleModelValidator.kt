package at.robthered.plan_me.features.data_source.domain.validation.update_section

import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError

interface UpdateSectionTitleModelValidator {
    operator fun invoke(updateSectionTitleModel: UpdateSectionTitleModel): UpdateSectionTitleModelError
}