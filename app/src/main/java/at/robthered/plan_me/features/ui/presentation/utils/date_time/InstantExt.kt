package at.robthered.plan_me.features.ui.presentation.utils.date_time

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Formats an [Instant] into a string suitable for a menu header.
 *
 * This function converts the [Instant] to the local date and time using the system's default
 * time zone and then formats it using the predefined [LocalDateTimeFormats.MenuHeaderFormat].
 *
 * @receiver The [Instant] to format.
 * @return A string representation of the [Instant] formatted for a menu header.
 */
fun Instant.toFormattedForMenuHeader(): String {
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = this.toLocalDateTime(timeZone)
    return LocalDateTimeFormats.MenuHeaderFormat.format(localDateTime)
}