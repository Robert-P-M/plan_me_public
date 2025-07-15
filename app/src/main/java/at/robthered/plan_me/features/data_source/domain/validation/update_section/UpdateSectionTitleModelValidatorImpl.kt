package at.robthered.plan_me.features.data_source.domain.validation.update_section

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidator

class UpdateSectionTitleModelValidatorImpl(
    private val sectionTitleValidator: SectionTitleValidator,
) : UpdateSectionTitleModelValidator {
    override operator fun invoke(updateSectionTitleModel: UpdateSectionTitleModel): UpdateSectionTitleModelError {
        return UpdateSectionTitleModelError(
            title = sectionTitleValidator(value = updateSectionTitleModel.title).getErrorOrNull()
        )
    }
}