package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import kotlinx.datetime.DayOfWeek

fun DayOfWeek.toUiText(): UiText {
    return when (this) {
        DayOfWeek.MONDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_monday
                )
        }

        DayOfWeek.TUESDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_tuesday
                )
        }

        DayOfWeek.WEDNESDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_wednesday
                )
        }

        DayOfWeek.THURSDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_thursday
                )
        }

        DayOfWeek.FRIDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_friday
                )
        }

        DayOfWeek.SATURDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_saturday
                )
        }

        DayOfWeek.SUNDAY -> {
            UiText
                .StringResource(
                    R.string.day_of_week_sunday
                )
        }
    }
}