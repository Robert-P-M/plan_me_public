package at.robthered.plan_me.features.data_source.domain.validation.add_section

import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError

interface AddSectionModelValidator {
    operator fun invoke(addSectionModel: AddSectionModel): AddSectionModelError
}