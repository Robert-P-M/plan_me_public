package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult

class TaskTitleValidatorImpl : TaskTitleValidator {
    override operator fun invoke(value: String): AppResult<String, TaskTitleValidationError> {
        val taskTitle = value.trim()
        if (taskTitle.isEmpty())
            return AppResult.Error(error = TaskTitleValidationError.EMPTY)

        return when {
            taskTitle.length > TaskTitleValidationError.MAX_LENGTH ->
                AppResult.Error(error = TaskTitleValidationError.OVERFLOW)

            taskTitle.length < TaskTitleValidationError.MIN_LENGTH ->
                AppResult.Error(error = TaskTitleValidationError.TOO_SHORT)

            else -> AppResult.Success(data = taskTitle)
        }
    }
}