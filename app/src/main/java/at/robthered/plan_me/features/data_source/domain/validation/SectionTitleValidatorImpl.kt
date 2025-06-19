package at.robthered.plan_me.features.data_source.domain.validation


import at.robthered.plan_me.features.common.domain.AppResult

class SectionTitleValidatorImpl : SectionTitleValidator {
    override fun invoke(value: String): AppResult<String, SectionTitleValidationError> {
        val sectionTitle = value.trim()
        if (sectionTitle.isEmpty())
            return AppResult.Error(error = SectionTitleValidationError.EMPTY)

        return when {
            sectionTitle.length > SectionTitleValidationError.MAX_LENGTH ->
                AppResult.Error(error = SectionTitleValidationError.OVERFLOW)

            sectionTitle.length < SectionTitleValidationError.MIN_LENGTH ->
                AppResult.Error(error = SectionTitleValidationError.TOO_SHORT)

            else -> AppResult.Success(data = sectionTitle)
        }
    }
}