package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.Time
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.CalendarItemsRow
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toDurationEnd
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toFullDateFormat
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toInt
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toText
import at.robthered.plan_me.features.ui.presentation.modifier.borderBottom
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PickedDateContainer(
    pickedDate: LocalDate?,
    pickedTime: Time?,
    isNotificationEnabled: Boolean,
    duration: Time?,
    isFullDay: Boolean,
    onRemoveTime: () -> Unit,
) {
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
            modifier = Modifier.borderBottom(
                strokeColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.3f
                )
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Today,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                text = pickedDate?.toFullDateFormat() ?: ""
            )
            AnimatedVisibility(
                visible = isNotificationEnabled,
                enter = fadeIn(
                    animationSpec = tween(
                        delayMillis = 80
                    )
                ) + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(
                    animationSpec = tween(
                        delayMillis = 80
                    )
                ),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Alarm,
                        contentDescription = null,
                    )
                }
            }
            AnimatedVisibility(
                visible = isFullDay,
                enter = fadeIn(
                    animationSpec = tween(
                        delayMillis = 80
                    )
                ) + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(
                    animationSpec = tween(
                        delayMillis = 80
                    )
                ),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                    )
                }
            }
            AnimatedVisibility(
                visible = pickedTime != null,
                enter = fadeIn(
                    animationSpec = tween(
                        delayMillis = 80
                    )
                ) + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(
                    animationSpec = tween(
                        delayMillis = 80
                    )
                ),
            ) {
                pickedTime?.let { time ->
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onRemoveTime),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        duration?.let {
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                text = time.toText() + " - " + (time.toInt() + it.toInt()).toDurationEnd()
                                    .toText()
                            )
                        } ?: Text(
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            text = time.toText()
                        )
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )

                    }

                }
            }

        }
    }
}