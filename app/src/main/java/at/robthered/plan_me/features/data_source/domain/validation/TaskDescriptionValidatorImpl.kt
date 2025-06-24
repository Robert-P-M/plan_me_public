package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult

class TaskDescriptionValidatorImpl : TaskDescriptionValidator {
    override operator fun invoke(value: String?): AppResult<String?, TaskDescriptionValidationError> {
        if (value == null) {
            return AppResult.Success(data = null)
        }

        val taskDescription = value.trim()

        return when {
            taskDescription.length > TaskDescriptionValidationError.MAX_LENGTH ->
                AppResult.Error(error = TaskDescriptionValidationError.OVERFLOW)

            taskDescription.length < TaskDescriptionValidationError.MIN_LENGTH ->
                AppResult.Error(error = TaskDescriptionValidationError.TOO_SHORT)

            else -> AppResult.Success(data = taskDescription)
        }
    }
}