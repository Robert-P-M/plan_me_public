package at.robthered.plan_me.features.task_schedule_picker_dialog.data

import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.DateTimeHelper
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.inOneMonth
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.isSunday
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.lastDayOfMonth
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarDay
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.FullCalendarWeekModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.mondayOfWeek
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.nextMonday
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.nextSaturday
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.saturdayOfWeek
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.sundayOfWeek
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.upcomingMondayInOneWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class DateTimeHelperImpl : DateTimeHelper {

    /**
     * Calculates the date for "Upcoming Monday".
     *
     * This function returns an [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.UpcomingMonday] object representing the Monday of the
     * calendar week immediately following the current week.
     *
     * @param localDate The reference date (e.g., today's date).
     * @return An [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate.UpcomingMonday] object for the upcoming Monday if [localDate] is not a Sunday.
     * Returns `null` if [localDate] is a Sunday, as "Upcoming Monday" is handled by other specific
     * upcoming date types in this context (e.g., [getUpcomingMondayInOneWeek]).
     */
    internal fun getUpcomingMonday(localDate: LocalDate): UpcomingDate.UpcomingMonday {
        return UpcomingDate.UpcomingMonday(localDate.nextMonday)
    }


    /**
     * Calculates the date for "Upcoming Monday in One Week".
     *
     * This function returns an [UpcomingDate.UpcomingMondayInOneWeek] object representing the Monday of the
     * week two weeks after the current week's Monday (i.e., the Monday following the next calendar week's Sunday).
     *
     * @param localDate The reference date (e.g., today's date).
     * @return An [UpcomingDate.UpcomingMondayInOneWeek] object for the Monday two weeks from now if [localDate] is a Sunday.
     * Returns `null` for all other days (Monday to Saturday).
     */
    internal fun getUpcomingMondayInOneWeek(localDate: LocalDate): UpcomingDate.UpcomingMondayInOneWeek? {
        return if (localDate.isSunday) {
            UpcomingDate.UpcomingMondayInOneWeek(localDate.upcomingMondayInOneWeek)
        } else null
    }

    /**
     * Calculates the date for "Tomorrow".
     *
     * This function returns an [UpcomingDate.Tomorrow] object representing the day after [localDate].
     *
     * @param [localDate] The reference date (e.g., today's date).
     * @return An [UpcomingDate.Tomorrow] object for the next day if [localDate] is not a Sunday.
     * Returns `null` if [localDate] is a Sunday, as "Tomorrow" (which would be Monday)
     * is handled by other specific upcoming date types in this context.
     */
    internal fun getTomorrowDate(localDate: LocalDate): UpcomingDate.Tomorrow? {
        return if (!localDate.isSunday) {
            UpcomingDate.Tomorrow(localDate.plus(period = DatePeriod(days = 1)))
        } else null
    }

    /**
     * Calculates the date for "This Weekend".
     *
     * This function returns an [UpcomingDate.UpcomingWeekend] object representing the Saturday
     * of the current calendar week.
     *
     * @param localDate The reference date (e.g., today's date).
     * @return An [UpcomingDate.UpcomingWeekend] object for the Saturday of the current week if
     * [localDate] is before the Friday of the current week.
     * Returns `null` if [localDate] is on or after the Friday of the current week,
     * indicating that "this weekend" is too close or has already passed in the context
     * of specific upcoming date options.
     */
    internal fun getThisWeekendDate(localDate: LocalDate): UpcomingDate.UpcomingWeekend? {
        return if (localDate < localDate.saturdayOfWeek.minus(period = DatePeriod(days = 1))) {
            UpcomingDate.UpcomingWeekend(localDate.saturdayOfWeek)
        } else {
            null
        }
    }

    /**
     * Calculates the date for "Next Weekend".
     *
     * This function returns an [UpcomingDate.NextWeekend] object representing the Saturday
     * of the calendar week immediately following the current week.
     *
     * @param localDate The reference date (e.g., today's date).
     * @return An [UpcomingDate.NextWeekend] object for the Saturday of the next calendar week.
     * This function always returns a non-null value as there is always a "next weekend".
     */
    internal fun getNextWeekendDate(localDate: LocalDate): UpcomingDate.NextWeekend {
        return UpcomingDate.NextWeekend(localDate.nextSaturday)
    }


    /**
     * Calculates the date for "In One Month".
     *
     * This function returns an [UpcomingDate.InOneMonth] object representing the date
     * exactly one month from the [localDate] input.
     *
     * @param localDate The reference date (e.g., today's date).
     * @return An [UpcomingDate.InOneMonth] object for the date one month later.
     * This function always returns a non-null value.
     */
    internal fun getInOneMonthDate(localDate: LocalDate): UpcomingDate.InOneMonth {
        return UpcomingDate.InOneMonth(localDate.inOneMonth)
    }

    /**
     * Generates a chronologically sorted list of relevant upcoming dates for a given [localDate].
     *
     * This function consolidates various specific upcoming date calculations by calling
     * internal helper functions. It then filters out any `null` results and sorts the remaining
     * [UpcomingDate] items by their [LocalDate].
     *
     * The list of upcoming dates may include:
     * - [UpcomingDate.Today] (always included)
     * - [UpcomingDate.Tomorrow] (if not Sunday)
     * - [UpcomingDate.UpcomingWeekend] (if [localDate] is before Friday of the current week)
     * - [UpcomingDate.UpcomingMonday] (if [localDate] is not Sunday)
     * - [UpcomingDate.UpcomingMondayInOneWeek] (if [localDate] is Sunday)
     * - [UpcomingDate.NextWeekend] (always included)
     * - [UpcomingDate.InOneMonth] (always included)
     *
     * @param localDate The reference date (e.g., today's date) from which to calculate upcoming dates.
     * @return A [kotlinx.coroutines.flow.Flow] emitting a [List] of [UpcomingDate] objects, sorted by their date.
     */
    override fun getUpcomingDates(localDate: LocalDate): Flow<List<UpcomingDate>> {
        val providers: List<(LocalDate) -> UpcomingDate?> = listOf(
            { d -> UpcomingDate.Today(d) },
            ::getTomorrowDate,
            ::getThisWeekendDate,
            ::getUpcomingMonday,
            ::getUpcomingMondayInOneWeek,
            ::getNextWeekendDate,
            ::getInOneMonthDate,
        )
        val dates = providers.mapNotNull { provider -> provider(localDate) }
            .sortedBy { it.localDate }
        return flowOf(dates)
    }


    /**
     * Generates a list of all [LocalDate] objects that form the bounding box for a given month's calendar view.
     *
     * The bounding box starts on the first Monday of the week containing the first day of the month,
     * and ends on the last Sunday of the week containing the last day of the month.
     * This ensures a complete 7-day week display for all days relevant to the month.
     *
     * @param monthNumber The month number (1-12) for which to get the bounding days.
     * @param year The year for which to get the bounding days.
     * @return A [List] of [LocalDate] objects representing all days within the calculated calendar bounding box.
     */
    internal fun getBoundingDaysForMonth(monthNumber: Int, year: Int): List<LocalDate> {

        val firstDayOfMonth = LocalDate(
            dayOfMonth = 1,
            monthNumber = monthNumber,
            year = year
        )
        val lastDayOfMonth = firstDayOfMonth.lastDayOfMonth

        val firstMonday = firstDayOfMonth.mondayOfWeek
        val lastSunday = lastDayOfMonth.sundayOfWeek

        return generateSequence(
            seed = firstMonday
        ) { date ->
            date
                .plus(
                    period = DatePeriod(days = 1)
                )
        }
            .takeWhile { it <= lastSunday }
            .toList()
    }

    /**
     * Organizes the bounding days of a month into a list of 7-day weeks.
     *
     * This function calls [getBoundingDaysForMonth] to get all relevant days for a month's calendar view,
     * and then chunks these days into sub-lists, where each sub-list represents a full week.
     *
     * @param monthNumber The month number (1-12) for which to get the weeks.
     * @param year The year for which to get the weeks.
     * @return A [List] of [List] of [LocalDate] objects, where each inner list represents a full week.
     */
    internal fun getWeeksForMonth(monthNumber: Int, year: Int): List<List<LocalDate>> {
        return getBoundingDaysForMonth(monthNumber, year)
            .chunked(size = 7)
    }

    /**
     * Constructs a [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel] for a given [LocalDate].
     *
     * This function takes a [LocalDate] (typically representing a day within the target month),
     * uses it to determine the month and year, and then generates a structured
     * [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel]. This model includes the month and year numbers, the mid-point of the month,
     * and a list of [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.FullCalendarWeekModel]s. Each [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.FullCalendarWeekModel] starts with a
     * [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarDay.WeekHeader] followed by 7 [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarDay.Day] objects for the week's days.
     *
     * @param localDate A [LocalDate] object from which the target month and year will be extracted.
     * @return A [at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel] representing the calendar view for the specified month.
     */
    override fun getCalendarMonth(localDate: LocalDate): CalendarMonthModel {
        val weeksForMonth =
            getWeeksForMonth(monthNumber = localDate.monthNumber, year = localDate.year)
        val fullCalendarWeeksModel: List<FullCalendarWeekModel> = weeksForMonth.map { week ->
            listOf(
                CalendarDay.WeekHeader(
                    localDate = week.first()
                )
            ) + week.map { day -> CalendarDay.Day(localDate = day) }
        }
        return CalendarMonthModel(
            monthNumber = localDate.monthNumber,
            midOfMonth = localDate,
            year = localDate.year,
            weeks = fullCalendarWeeksModel
        )
    }


}