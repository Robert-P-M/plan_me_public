package at.robthered.plan_me.features.data_source.domain.model.add_section

import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidationError

data class AddSectionModelError(
    val title: SectionTitleValidationError? = null,
)