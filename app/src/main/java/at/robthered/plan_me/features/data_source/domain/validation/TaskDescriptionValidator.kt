package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult

interface TaskDescriptionValidator {
    operator fun invoke(value: String?): AppResult<String?, TaskDescriptionValidationError>
}