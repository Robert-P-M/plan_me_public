package at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model

import kotlinx.datetime.LocalDate

sealed class CalendarDay(
    open val localDate: LocalDate,
) {
    data class WeekHeader(
        override val localDate: LocalDate,
    ) : CalendarDay(localDate)

    data class Day(
        override val localDate: LocalDate,
    ) : CalendarDay(localDate)
}