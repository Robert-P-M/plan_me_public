package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult

class TaskScheduleStartDateEpochDaysValidatorImpl : TaskScheduleStartDateEpochDaysValidator {
    override fun invoke(value: Int?): AppResult<Int, TaskScheduleDateEpochDaysError> {
        return when {
            value == null -> {
                AppResult.Error(error = TaskScheduleDateEpochDaysError.EMPTY)
            }

            value < TaskScheduleDateEpochDaysError.MIN_VALID_EPOCH_DAYS -> {
                AppResult.Error(error = TaskScheduleDateEpochDaysError.TOO_SHORT)
            }

            value > TaskScheduleDateEpochDaysError.MAX_VALID_EPOCH_DAYS -> {
                AppResult.Error(error = TaskScheduleDateEpochDaysError.OVERFLOW)
            }

            else -> AppResult.Success(value)
        }
    }
}