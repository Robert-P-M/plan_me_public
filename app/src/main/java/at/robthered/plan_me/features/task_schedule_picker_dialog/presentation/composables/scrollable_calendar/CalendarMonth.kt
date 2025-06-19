package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.isoWeekNumber
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarDay
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.FullCalendarWeekModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.TaskSchedulePickerDialogUiAction
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toUiText
import kotlinx.datetime.LocalDate

fun LazyListScope.calendarMonth(
    pagedCalendarMonth: LazyPagingItems<CalendarMonthModel>,
    pickedDate: LocalDate?,
    onAction: (TaskSchedulePickerDialogUiAction) -> Unit,
    currentLocalDate: LocalDate,
) {
    items(count = pagedCalendarMonth.itemCount) { index ->
        val strokeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        val density = LocalDensity.current
        val item = pagedCalendarMonth[index]
        if (item != null) {

            var boxWidth by remember {
                mutableIntStateOf(0)
            }

            var monthHeaderBottom by remember {
                mutableIntStateOf(0)
            }

            val lineCenter by remember(boxWidth) {
                derivedStateOf {
                    with(density) {
                        boxWidth.div(8).toDp().toPx()
                    }
                }
            }

            val saturdayLeftX by remember(boxWidth) {
                derivedStateOf {
                    with(density) {
                        boxWidth.div(8).times(6).toDp().toPx()
                    }
                }
            }

            val sundayLeftX by remember(boxWidth) {
                derivedStateOf {
                    with(density) {
                        boxWidth.div(8).times(7).toDp().toPx()
                    }
                }
            }

            val lineBottom by remember(monthHeaderBottom) {
                derivedStateOf {
                    with(density) {
                        monthHeaderBottom.toDp().toPx()
                    }
                }
            }

            val saturdayColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)

            val sundayColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)

            val dayWidth by remember(boxWidth) {
                derivedStateOf {
                    with(density) {
                        boxWidth.div(8).toDp().toPx()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .onGloballyPositioned {
                        boxWidth = it.size.width
                    }
                    .drawBehind {
                        drawLine(
                            color = strokeColor,
                            start = Offset(
                                x = lineCenter,
                                y = lineBottom,
                            ),
                            end = Offset(
                                x = lineCenter,
                                y = this.size.height
                            )
                        )
                        drawRect(
                            color = saturdayColor,
                            topLeft = Offset(
                                x = saturdayLeftX,
                                y = lineBottom,
                            ),
                            size = Size(
                                width = dayWidth,
                                height = this.size.height - lineBottom
                            )
                        )
                        drawRect(
                            color = sundayColor,
                            topLeft = Offset(
                                x = sundayLeftX,
                                y = lineBottom,
                            ),
                            size = Size(
                                width = dayWidth,
                                height = this.size.height - lineBottom
                            )
                        )
                    }
            ) {
                Column(
                    modifier = Modifier

                ) {
                    CalendarItemsRow(
                        modifier = Modifier
                            .background(
                                color = sundayColor
                            )
                            .onGloballyPositioned {
                                monthHeaderBottom =
                                    it.positionInParent().y.toInt() + it.size.height
                            }
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp),
                            text = "${
                                item.midOfMonth.month.toUiText().asString()
                            } ${item.midOfMonth.year}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                    item.weeks.map { week: FullCalendarWeekModel ->
                        CalendarItemsRow(
                            modifier = Modifier
                        ) {
                            week.forEach { calendarDay ->
                                when (calendarDay) {
                                    is CalendarDay.Day -> {
                                        val datePickedAlpha by animateFloatAsState(
                                            targetValue = if (calendarDay.localDate == pickedDate) 0.5f else 0f,
                                            animationSpec = tween(
                                                delayMillis = 50,
                                                durationMillis = 150
                                            ),
                                            label = "${calendarDay.localDate}-datePickedAlpha"
                                        )
                                        CalendarItemContainer(
                                            modifier = Modifier
                                                .clickable {
                                                    onAction(
                                                        TaskSchedulePickerDialogUiAction.OnPickLocalDate(
                                                            calendarDay.localDate
                                                        )
                                                    )
                                                }
                                                .then(
                                                    if (currentLocalDate == calendarDay.localDate) {
                                                        Modifier
                                                            .background(
                                                                color = MaterialTheme.colorScheme.primary.copy(
                                                                    alpha = 0.3f
                                                                ),
                                                                shape = RoundedCornerShape(
                                                                    size = 4.dp
                                                                )
                                                            )
                                                    } else Modifier
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.tertiary.copy(
                                                        alpha = datePickedAlpha
                                                    ),
                                                    shape = RoundedCornerShape(
                                                        size = 4.dp
                                                    )
                                                )
                                        ) {

                                            Text(
                                                modifier = Modifier
                                                    .then(
                                                        if (item.monthNumber == calendarDay.localDate.monthNumber) {
                                                            Modifier
                                                        } else {
                                                            Modifier.alpha(0.3f)
                                                        }
                                                    ),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodyMedium,
                                                text = calendarDay.localDate.dayOfMonth.toString()
                                                    .padStart(2, '0'),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    is CalendarDay.WeekHeader -> {
                                        CalendarItemContainer {
                                            Text(
                                                modifier = Modifier
                                                    .alpha(alpha = 0.7f),
                                                textAlign = TextAlign.Center,
                                                text = calendarDay.localDate.isoWeekNumber.toString()
                                                    .padStart(2, '0')
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}