package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.ValidationError
import kotlinx.datetime.LocalDate

enum class TaskScheduleDateEpochDaysError : ValidationError {
    TOO_SHORT, OVERFLOW, EMPTY;

    companion object {
        val MIN_VALID_EPOCH_DAYS =
            LocalDate(year = 1900, monthNumber = 1, dayOfMonth = 1).toEpochDays()
        val MAX_VALID_EPOCH_DAYS =
            LocalDate(year = 2100, monthNumber = 12, dayOfMonth = 31).toEpochDays()
    }
}