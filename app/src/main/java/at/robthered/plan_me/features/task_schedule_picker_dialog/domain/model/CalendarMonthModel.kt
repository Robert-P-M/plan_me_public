package at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model

import kotlinx.datetime.LocalDate

data class CalendarMonthModel(
    val midOfMonth: LocalDate,
    val monthNumber: Int,
    val year: Int,
    val weeks: List<FullCalendarWeekModel>,
)