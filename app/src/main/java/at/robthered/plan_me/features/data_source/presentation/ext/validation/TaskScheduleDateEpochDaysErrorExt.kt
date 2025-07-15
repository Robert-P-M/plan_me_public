package at.robthered.plan_me.features.data_source.presentation.ext.validation

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.validation.TaskScheduleDateEpochDaysError
import kotlinx.datetime.LocalDate

fun TaskScheduleDateEpochDaysError.toUiText(): UiText {
    return when (this) {
        TaskScheduleDateEpochDaysError.TOO_SHORT ->
            UiText
                .StringResource(
                    id = R.string.task_schedule_date_epoch_days_error_too_short,
                    args = listOf(LocalDate.fromEpochDays(TaskScheduleDateEpochDaysError.MIN_VALID_EPOCH_DAYS))
                )

        TaskScheduleDateEpochDaysError.OVERFLOW ->
            UiText
                .StringResource(
                    id = R.string.task_schedule_date_epoch_days_error_overflow,
                    args = listOf(LocalDate.fromEpochDays(TaskScheduleDateEpochDaysError.MAX_VALID_EPOCH_DAYS))
                )

        TaskScheduleDateEpochDaysError.EMPTY ->
            UiText
                .StringResource(
                    id = R.string.task_schedule_date_epoch_days_error_empty
                )
    }
}