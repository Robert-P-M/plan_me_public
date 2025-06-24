package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toUiText
import kotlinx.datetime.LocalDate

fun UpcomingDate.toUiText(): UiText {
    return when (this) {
        is UpcomingDate.Today -> UiText
            .StringResource(
                id = R.string.upcoming_date_today
            )

        is UpcomingDate.Tomorrow -> UiText
            .StringResource(
                id = R.string.upcoming_date_tomorrow
            )

        is UpcomingDate.UpcomingWeekend -> UiText
            .StringResource(
                id = R.string.upcoming_date_upcoming_weekend
            )

        is UpcomingDate.NextWeekend -> UiText
            .StringResource(
                id = R.string.upcoming_date_next_weekend
            )

        is UpcomingDate.UpcomingMonday -> UiText
            .StringResource(
                id = R.string.upcoming_date_upcoming_monday
            )

        is UpcomingDate.UpcomingMondayInOneWeek -> UiText
            .StringResource(
                id = R.string.upcoming_date_monday_in_one_week
            )

        is UpcomingDate.InOneMonth -> UiText
            .StringResource(
                id = R.string.upcoming_date_in_one_month,
            )
    }
}

fun UpcomingDate.imageVector(): ImageVector {
    return when (this) {
        is UpcomingDate.InOneMonth -> Icons.Outlined.CalendarMonth
        is UpcomingDate.NextWeekend -> Icons.Outlined.Weekend
        is UpcomingDate.Today -> Icons.Outlined.Today
        is UpcomingDate.Tomorrow -> Icons.Outlined.Today
        is UpcomingDate.UpcomingMonday -> Icons.Outlined.Today
        is UpcomingDate.UpcomingMondayInOneWeek -> Icons.Outlined.Today
        is UpcomingDate.UpcomingWeekend -> Icons.Outlined.Weekend
    }
}

@Composable
fun UpcomingDate.toShortText(localDate: LocalDate): String {
    return when (this) {
        is UpcomingDate.Today -> localDate.dayOfWeek.toUiText().asString().take(3)
        is UpcomingDate.Tomorrow -> localDate.dayOfWeek.toUiText().asString().take(3)
        is UpcomingDate.UpcomingWeekend -> {
            localDate.dayOfWeek.toUiText().asString()
                .take(3) + ". " + localDate.dayOfMonth.toString()
                .padStart(2, '0') + ". " + localDate.month.toUiText().asString().take(3)
        }

        is UpcomingDate.NextWeekend -> {
            localDate.dayOfWeek.toUiText().asString()
                .take(3) + ". " + localDate.dayOfMonth.toString()
                .padStart(2, '0') + ". " + localDate.month.toUiText().asString().take(3)
        }

        is UpcomingDate.UpcomingMonday -> {
            localDate.dayOfWeek.toUiText().asString()
                .take(3) + ". " + localDate.dayOfMonth.toString()
                .padStart(2, '0') + ". " + localDate.month.toUiText().asString().take(3)
        }

        is UpcomingDate.UpcomingMondayInOneWeek -> {
            localDate.dayOfWeek.toUiText().asString()
                .take(3) + ". " + localDate.dayOfMonth.toString()
                .padStart(2, '0') + ". " + localDate.month.toUiText().asString().take(3)
        }

        is UpcomingDate.InOneMonth -> {
            localDate.dayOfWeek.toUiText().asString()
                .take(3) + ". " + localDate.dayOfMonth.toString()
                .padStart(2, '0') + ". " + localDate.month.toUiText().asString().take(3)
        }
    }
}