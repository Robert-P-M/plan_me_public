package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toFullDateFormat
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toUiText
import at.robthered.plan_me.features.ui.presentation.modifier.borderTop
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.calendarHeaderWeeks(
    modifier: Modifier = Modifier,
    currentLocalDate: LocalDate,
    setHeaderHeight: (Int) -> Unit,
    scrollUp: () -> Unit,
) {
    stickyHeader {
        Column(
            modifier = modifier
                .borderTop(strokeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                .animateContentSize()
                .background(
                    BottomSheetDefaults.ContainerColor
                )
                .animateContentSize()
                .onGloballyPositioned {
                    setHeaderHeight(it.size.height)
                }
                .shadow(elevation = 1.dp)
        ) {
            CalendarItemsRow {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Outlined.Today,
                    contentDescription = null
                )
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentLocalDate.toFullDateFormat(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Icon(
                    modifier = Modifier
                        .clickable(onClick = scrollUp),
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = null
                )
            }
            CalendarItemsRow {
                CalendarItemContainer {
                    Text(
                        text = stringResource(R.string.calendar_week_short),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                DayOfWeek.entries.forEach { dayOfWeek: DayOfWeek ->
                    CalendarItemContainer(
                        modifier = Modifier
                            .then(
                                if (currentLocalDate.dayOfWeek.name == dayOfWeek.name) {
                                    Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.3f
                                            ),
                                            shape = RoundedCornerShape(size = 4.dp)
                                        )
                                } else Modifier
                            ),
                    ) {
                        Text(
                            text = dayOfWeek.toUiText().asString().take(2),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
            )
        }

    }
}