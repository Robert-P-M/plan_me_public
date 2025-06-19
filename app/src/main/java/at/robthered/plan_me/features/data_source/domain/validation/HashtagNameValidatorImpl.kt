package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult

class HashtagNameValidatorImpl : HashtagNameValidator {
    override operator fun invoke(value: String): AppResult<String, HashtagNameValidationError> {
        val newHashtagName = value.trim()
        if (newHashtagName.isEmpty()) {
            return AppResult.Error(error = HashtagNameValidationError.EMPTY)
        }
        return when {
            newHashtagName.length > HashtagNameValidationError.MAX_LENGTH ->
                AppResult.Error(error = HashtagNameValidationError.OVERFLOW)

            newHashtagName.length < HashtagNameValidationError.MIN_LENGTH ->
                AppResult.Error(error = HashtagNameValidationError.TOO_SHORT)

            else -> AppResult.Success(data = newHashtagName)

        }
    }
}