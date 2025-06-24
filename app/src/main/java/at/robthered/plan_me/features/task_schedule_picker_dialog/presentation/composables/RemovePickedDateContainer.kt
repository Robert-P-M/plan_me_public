package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotInterested
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.CalendarItemsRow
import kotlinx.datetime.LocalDate

fun LazyListScope.removePickedDateContainer(
    pickedDate: LocalDate?,
    onRemove: () -> Unit,
) {
    item {
        AnimatedVisibility(
            visible = pickedDate != null,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 100
                )
            ) + expandVertically(),
            exit = fadeOut() + shrinkVertically(
                animationSpec = tween(
                    delayMillis = 100
                )
            )
        ) {
            CalendarItemsRow(
                modifier = Modifier
                    .clickable(onClick = onRemove),
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotInterested,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    text = "No date"
                )

            }
        }
    }
}