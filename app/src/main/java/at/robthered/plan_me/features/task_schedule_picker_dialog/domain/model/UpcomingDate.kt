package at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model

import kotlinx.datetime.LocalDate

sealed class UpcomingDate(
    open val localDate: LocalDate,
) {

    data class Today(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

    data class Tomorrow(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

    data class UpcomingWeekend(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

    data class NextWeekend(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

    data class UpcomingMonday(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

    data class UpcomingMondayInOneWeek(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

    data class InOneMonth(
        override val localDate: LocalDate,
    ) : UpcomingDate(localDate = localDate)

}