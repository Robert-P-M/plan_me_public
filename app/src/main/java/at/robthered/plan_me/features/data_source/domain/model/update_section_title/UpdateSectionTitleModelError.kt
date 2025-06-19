package at.robthered.plan_me.features.data_source.domain.model.update_section_title

import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidationError

data class UpdateSectionTitleModelError(
    val title: SectionTitleValidationError? = null,
)