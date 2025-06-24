package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.CalendarItemsRow
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.model.imageVector
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.model.toShortText
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.model.toUiText
import kotlinx.datetime.LocalDate

fun LazyListScope.upcomingDates(
    upcomingDates: List<UpcomingDate>,
    onPickLocalDate: (LocalDate?) -> Unit,
    pickedDate: LocalDate?,
) {
    itemsIndexed(items = upcomingDates) { index, upcomingDate ->
        val datePickedAlpha by animateFloatAsState(
            targetValue = if (upcomingDate.localDate == pickedDate) 0.2f else 0f,
            animationSpec = tween(
                delayMillis = 50,
                durationMillis = 150
            ),
            label = "${upcomingDate.localDate}-datePickedAlpha"
        )
        CalendarItemsRow(
            modifier = Modifier
                .clickable {
                    onPickLocalDate(upcomingDate.localDate)
                }
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = datePickedAlpha
                    ),
                ),
        ) {
            Icon(
                imageVector = upcomingDate.imageVector(),
                contentDescription = upcomingDate.toUiText().asString(),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),

                )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                text = upcomingDate.toUiText().asString()
            )
            Text(
                text = upcomingDate.toShortText(localDate = upcomingDate.localDate)
            )
        }

    }
}