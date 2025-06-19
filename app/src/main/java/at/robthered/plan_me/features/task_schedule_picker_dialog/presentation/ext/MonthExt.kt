package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import kotlinx.datetime.Month

fun Month.toUiText(): UiText {
    return when (this) {
        Month.JANUARY -> {
            UiText.StringResource(
                id = R.string.month_january
            )
        }

        Month.FEBRUARY -> {
            UiText.StringResource(
                id = R.string.month_february
            )
        }

        Month.MARCH -> {
            UiText.StringResource(
                id = R.string.month_march
            )
        }

        Month.APRIL -> {
            UiText.StringResource(
                id = R.string.month_april
            )
        }

        Month.MAY -> {
            UiText.StringResource(
                id = R.string.month_may
            )
        }

        Month.JUNE -> {
            UiText.StringResource(
                id = R.string.month_june
            )
        }

        Month.JULY -> {
            UiText.StringResource(
                id = R.string.month_july
            )
        }

        Month.AUGUST -> {
            UiText.StringResource(
                id = R.string.month_august
            )
        }

        Month.SEPTEMBER -> {
            UiText.StringResource(
                id = R.string.month_september
            )
        }

        Month.OCTOBER -> {
            UiText.StringResource(
                id = R.string.month_october
            )
        }

        Month.NOVEMBER -> {
            UiText.StringResource(
                id = R.string.month_november
            )
        }

        Month.DECEMBER -> {
            UiText.StringResource(
                id = R.string.month_december
            )
        }
    }
}