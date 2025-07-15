package at.robthered.plan_me.features.data_source.domain.validation.add_section

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidator

class AddSectionModelValidatorImpl(
    private val sectionTitleValidator: SectionTitleValidator,
) : AddSectionModelValidator {
    override operator fun invoke(addSectionModel: AddSectionModel): AddSectionModelError {
        return AddSectionModelError(
            title = sectionTitleValidator(value = addSectionModel.title).getErrorOrNull()
        )
    }
}