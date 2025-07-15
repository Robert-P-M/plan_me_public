package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.AppResult

interface TaskScheduleStartDateEpochDaysValidator {
    operator fun invoke(value: Int?): AppResult<Int, TaskScheduleDateEpochDaysError>
}