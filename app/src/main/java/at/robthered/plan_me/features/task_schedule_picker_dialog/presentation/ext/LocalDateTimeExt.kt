package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext

import androidx.compose.runtime.Composable
import at.robthered.plan_me.features.common.presentation.asString
import kotlinx.datetime.LocalDateTime

@Composable
fun LocalDateTime.toFullDateFormat(): String {
    return this.dayOfMonth.toString().padStart(2, '0') + ". " + this.month.toUiText()
        .asString() + " " + this.year.toString()
}