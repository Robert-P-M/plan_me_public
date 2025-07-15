package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext

import androidx.compose.runtime.Composable
import at.robthered.plan_me.features.common.presentation.asString
import kotlinx.datetime.LocalDate

@Composable
fun LocalDate.toFullDateFormat(): String {
    return this.dayOfMonth.toString().padStart(2, '0') + ". " + this.month.toUiText()
        .asString() + " " + this.year.toString()
}

fun Int.toLocalDate(): LocalDate {
    return LocalDate.fromEpochDays(this)
}