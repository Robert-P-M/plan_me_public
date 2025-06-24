package at.robthered.plan_me.features.ui.presentation.utils.date_time

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

/**
 * An object containing predefined [LocalDateTime.Format] instances for common date and time formatting patterns.
 */
object LocalDateTimeFormats {
    val MenuHeaderFormat = LocalDateTime.Format {
        dayOfMonth()
        char('.')
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        year()
        chars(" - ")
        char(' ')
        hour()
        char(':')
        minute()
    }

    val DateTimeFormatWithWeekday = LocalDateTime.Format {
        dayOfWeek(names = DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(". ")
        dayOfMonth()
        char('.')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
        chars(" - ")
        char(' ')
        hour()
        char(':')
        minute()
    }
    val DateTimeFormat = LocalDateTime.Format {
        dayOfMonth()
        char('.')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
        chars(" - ")
        char(' ')
        hour()
        char(':')
        minute()
    }
    val DateFormat = LocalDateTime.Format {
        dayOfMonth()
        char('.')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
    }
}