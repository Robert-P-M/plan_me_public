package at.robthered.plan_me.features.task_schedule_picker_dialog.domain

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface DateTimeHelper {
    fun getCalendarMonth(localDate: LocalDate): CalendarMonthModel
    fun getUpcomingDates(localDate: LocalDate): Flow<List<UpcomingDate>>
}