package at.robthered.plan_me.features.task_schedule_picker_dialog.domain

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

val LocalDate.lastDayOfMonth: LocalDate
    get() {
        return LocalDate(
            year = this.year,
            monthNumber = this.monthNumber,
            dayOfMonth = 1
        )
            .plus(
                period = DatePeriod(months = 1)
            )
            .minus(
                period = DatePeriod(days = 1)
            )
    }

val LocalDate.mondayOfWeek: LocalDate
    get() {
        return this.minus(
            period = DatePeriod(
                days = this.dayOfWeek.isoDayNumber - 1
            )
        )
    }

val LocalDate.sundayOfWeek: LocalDate
    get() {
        return this.plus(
            period = DatePeriod(
                days = DayOfWeek.SUNDAY.isoDayNumber - this.dayOfWeek.isoDayNumber
            )
        )
    }
val LocalDate.isoWeekYear: Int
    get() {
        val thursdayOfWeek = this.plus(
            period = DatePeriod(
                days = DayOfWeek.THURSDAY.isoDayNumber - this.dayOfWeek.isoDayNumber
            )
        )
        return thursdayOfWeek.year
    }

val LocalDate.isoWeekNumber: Int
    get() {
        val currentWeekThursday =
            this.plus(DatePeriod(days = DayOfWeek.THURSDAY.isoDayNumber - this.dayOfWeek.isoDayNumber))
        val isoYear = currentWeekThursday.year

        val jan4thOfIsoYear = LocalDate(isoYear, 1, 4)
        val firstThursdayOfIsoYear =
            jan4thOfIsoYear.plus(DatePeriod(days = DayOfWeek.THURSDAY.isoDayNumber - jan4thOfIsoYear.dayOfWeek.isoDayNumber))

        val daysDiff = currentWeekThursday.toEpochDays() - firstThursdayOfIsoYear.toEpochDays()
        return (daysDiff / 7) + 1
    }


val LocalDate.midOfMonth: LocalDate
    get() = LocalDate(year = this.year, monthNumber = this.monthNumber, dayOfMonth = 15)


val LocalDate.nextMonday: LocalDate
    get() = this.mondayOfWeek.plus(period = DatePeriod(days = 7))


val LocalDate.upcomingMondayInOneWeek: LocalDate
    get() = this.mondayOfWeek.nextMonday.nextMonday

val LocalDate.tomorrow: LocalDate
    get() = this.plus(period = DatePeriod(days = 1))


val LocalDate.saturdayOfWeek: LocalDate
    get() = this.mondayOfWeek.plus(period = DatePeriod(days = 5))


val LocalDate.nextSaturday: LocalDate
    get() = this.saturdayOfWeek.plus(DatePeriod(days = 7))
val LocalDate.isSunday: Boolean
    get() = this.dayOfWeek == DayOfWeek.SUNDAY

val LocalDate.inOneMonth: LocalDate
    get() = this.plus(period = DatePeriod(months = 1))


fun String?.toLocalDateOrNull(): LocalDate? {
    return try {
        val trimmed = this?.trim()

        if (trimmed.isNullOrEmpty() || trimmed.equals("null", ignoreCase = true)) {
            null
        } else {
            LocalDate.parse(trimmed)
        }
    } catch (e: IllegalArgumentException) {
        null
    }
}