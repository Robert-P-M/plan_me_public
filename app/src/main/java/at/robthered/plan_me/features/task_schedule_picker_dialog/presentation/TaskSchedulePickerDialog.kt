package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.AvTimer
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModelError
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.Time
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.PickedDateContainer
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.removePickedDateContainer
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.CalendarItemsRow
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.calendarHeaderWeeks
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.calendarMonth
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.scrollable_calendar.rememberScrollableCalendarListState
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.time_picker.TimePickerDialog
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.composables.upcomingDates
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toFullDateFormat
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toText
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.TaskSchedulePickerDialogNavigationEvent
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.TaskSchedulePickerNavigationActions
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.modifier.borderTop
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionRequester() {
    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    )
    val useExactAlarmPermissionState = rememberPermissionState(
        permission = Manifest.permission.USE_EXACT_ALARM
    )
    val scheduleExactAlarmPermissionState = rememberPermissionState(
        permission = Manifest.permission.SCHEDULE_EXACT_ALARM
    )

    LaunchedEffect(Unit) {
        if (!notificationPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        if (!useExactAlarmPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        if (!scheduleExactAlarmPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
fun TaskSchedulePickerDialogRoot(
    modifier: Modifier = Modifier,
    taskSchedulePickerNavigationActions: TaskSchedulePickerNavigationActions,
    taskSchedulePickerDialogViewModel: TaskSchedulePickerDialogViewModel = koinViewModel<TaskSchedulePickerDialogViewModel>(),
) {

    ObserveAsEvents(taskSchedulePickerDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            TaskSchedulePickerDialogNavigationEvent.OnNavigateBack -> {
                taskSchedulePickerNavigationActions.onNavigateBack()
            }
        }

    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        NotificationPermissionRequester()
    }

    val pagedCalendarMonth =
        taskSchedulePickerDialogViewModel.pagedCalendarMonth.collectAsLazyPagingItems()
    val upcomingDates by taskSchedulePickerDialogViewModel.upcomingDates.collectAsStateWithLifecycle()
    val pickedDate by taskSchedulePickerDialogViewModel.pickedDate.collectAsStateWithLifecycle()
    val pickedTime by taskSchedulePickerDialogViewModel.pickedTime.collectAsStateWithLifecycle()
    val isNotificationEnabled by taskSchedulePickerDialogViewModel.isNotificationEnabled.collectAsStateWithLifecycle()
    val pickedDuration by taskSchedulePickerDialogViewModel.pickedDuration.collectAsStateWithLifecycle()
    val isFullDay by taskSchedulePickerDialogViewModel.isFullDay.collectAsStateWithLifecycle()
    val addTaskScheduleEventModelError by taskSchedulePickerDialogViewModel.addTaskScheduleEventModelError.collectAsStateWithLifecycle()
    val didModelChange by taskSchedulePickerDialogViewModel.didModelChange.collectAsStateWithLifecycle()
    val hasExactAlarmPermission by taskSchedulePickerDialogViewModel.hasExactAlarmPermission.collectAsStateWithLifecycle()


    TaskSchedulePickerDialog(
        modifier = modifier,
        pagedCalendarMonth = pagedCalendarMonth,
        upcomingDates = upcomingDates,
        pickedDate = pickedDate,
        pickedTime = pickedTime,
        isNotificationEnabled = isNotificationEnabled,
        pickedDuration = pickedDuration,
        isFullDay = isFullDay,
        addTaskScheduleEventModelError = addTaskScheduleEventModelError,
        didModelChange = didModelChange,
        onAction = taskSchedulePickerDialogViewModel::onAction
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TaskSchedulePickerDialog(
    modifier: Modifier = Modifier,
    pagedCalendarMonth: LazyPagingItems<CalendarMonthModel>,
    onAction: (TaskSchedulePickerDialogUiAction) -> Unit,
    upcomingDates: List<UpcomingDate>,
    pickedDate: LocalDate?,
    isNotificationEnabled: Boolean,
    pickedTime: Time?,
    isFullDay: Boolean,
    pickedDuration: Time?,
    addTaskScheduleEventModelError: AddTaskScheduleEventModelError,
    didModelChange: Boolean,
) {

    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didModelChange,
        onNavigateBack = {
            onAction(
                TaskSchedulePickerDialogUiAction
                    .OnNavigateBack
            )
        },
    )
    val abortDialogText = UiText.StringResource(
        id = R.string.add_task_dialog_confirm_back_navigation_text,
        args = listOf(stringResource(R.string.add_task_dialog_confirm_back_navigation_text_lost))
    )

    if (appSheetState.showConfirmationDialog) {
        AppAbortDialog(
            onDismissRequest = {
                appSheetState.cancelDiscardChanges()
            },
            onAccept = {
                appSheetState.confirmDiscardAndNavigateBack()
            },
            title = stringResource(R.string.add_task_dialog_confirm_back_navigation_title),
            text = abortDialogText.asAnnotatedString(color = MaterialTheme.colorScheme.error)
        )
    }

    val scrollableCalendarListState = rememberScrollableCalendarListState()

    val scope = rememberCoroutineScope()
    val currentLocalDateTime = remember {
        Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
    }
    val currentLocalDate = remember {
        currentLocalDateTime.date
    }
    var isTimePickerVisible by remember {
        mutableStateOf(false)
    }


    TimePickerDialog(
        isVisible = isTimePickerVisible,
        title = stringResource(R.string.time_picker_dialog_title),
        initialTime = pickedTime ?: Time(
            hours = currentLocalDateTime.hour,
            minutes = currentLocalDateTime.minute
        ),
        onDismissRequest = {
            isTimePickerVisible = false
        },
        onPickTime = {
            onAction(
                TaskSchedulePickerDialogUiAction
                    .OnPickTime(
                        time = it
                    )
            )
        }
    )
    var isDurationPickerVisible by remember {
        mutableStateOf(false)
    }


    TimePickerDialog(
        isVisible = isDurationPickerVisible,
        title = stringResource(R.string.duration_picker_dialog_title),
        initialTime = pickedDuration ?: Time(),
        onDismissRequest = {
            isDurationPickerVisible = false
        },
        onPickTime = {
            onAction(
                TaskSchedulePickerDialogUiAction
                    .OnPickDuration(
                        duration = it
                    )
            )
        }
    )


    ModalBottomSheet(
        modifier = modifier
            .fillMaxSize(),
        onDismissRequest = {
            appSheetState.requestHide()
        },
        dragHandle = null,
        sheetState = appSheetState.sheetState
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    state = scrollState,
                    orientation = Orientation.Vertical
                ),
        ) {
            AppModalTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                title = stringResource(R.string.task_schedule_picker_dialog_modal_title),
                leadingIcon = {
                },
                trailingIcon = {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            appSheetState.requestHide()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            PickedDateContainer(
                pickedDate = pickedDate,
                pickedTime = pickedTime,
                isNotificationEnabled = isNotificationEnabled,
                duration = pickedDuration,
                isFullDay = isFullDay,
                onRemoveTime = {
                    onAction(
                        TaskSchedulePickerDialogUiAction.OnPickTime(null)
                    )
                }
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = scrollableCalendarListState.lazyListState,
                flingBehavior = scrollableCalendarListState.flingBehavior,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                upcomingDates(
                    upcomingDates = upcomingDates,
                    pickedDate = pickedDate,
                    onPickLocalDate = { localDate: LocalDate? ->
                        onAction(
                            TaskSchedulePickerDialogUiAction.OnPickLocalDate(localDate)
                        )
                    }
                )
                removePickedDateContainer(
                    pickedDate = pickedDate,
                    onRemove = {
                        onAction(
                            TaskSchedulePickerDialogUiAction
                                .OnPickLocalDate(null)
                        )
                    }
                )
                calendarHeaderWeeks(
                    modifier = Modifier,
                    currentLocalDate = currentLocalDate,
                    setHeaderHeight = scrollableCalendarListState.setHeaderHeight,
                    scrollUp = {
                        scope.launch {
                            scrollableCalendarListState
                                .lazyListState
                                .animateScrollToItem(upcomingDates.size + 1)
                        }
                    }
                )
                calendarMonth(
                    pagedCalendarMonth = pagedCalendarMonth,
                    pickedDate = pickedDate,
                    onAction = onAction,
                    currentLocalDate = currentLocalDate,
                )
            }


            Column(
                modifier = Modifier
                    .borderTop(strokeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    .padding(top = 8.dp)
                    .padding(vertical = 16.dp)
            ) {
                val rowState = rememberLazyListState()
                var showFullToggleButtonContent by remember {
                    mutableStateOf(true)
                }
                LazyRow(
                    state = rowState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {

                        ToggleButton(
                            checked = showFullToggleButtonContent,
                            onCheckedChange = { showFullToggleButtonContent = it }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        }
                    }
                    item {

                        ToggleButton(
                            checked = pickedDate != null,
                            onCheckedChange = {},
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                            AnimatedVisibility(
                                visible = showFullToggleButtonContent,
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        text = pickedDate?.toFullDateFormat()
                                            ?: stringResource(R.string.task_schedule_picker_dialog_pick_a_date)
                                    )
                                    if (pickedDate != null) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.clickable {
                                                onAction(
                                                    TaskSchedulePickerDialogUiAction
                                                        .OnPickLocalDate(null)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {

                        ToggleButton(
                            checked = pickedTime != null,
                            enabled = pickedDate != null,
                            onCheckedChange = {

                                isTimePickerVisible = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                            AnimatedVisibility(
                                visible = showFullToggleButtonContent,
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        text = pickedTime?.toText()
                                            ?: stringResource(R.string.task_schedule_picker_dialog_pick_a_time)
                                    )
                                    if (pickedTime != null) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.clickable {
                                                onAction(
                                                    TaskSchedulePickerDialogUiAction
                                                        .OnPickTime(null)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {

                        ToggleButton(
                            modifier = Modifier.weight(1f),
                            enabled = pickedDate != null,
                            checked = isNotificationEnabled,
                            onCheckedChange = {
                                onAction(
                                    TaskSchedulePickerDialogUiAction
                                        .OnToggleNotificationEnabled
                                )
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Alarm,
                                contentDescription = null,
                                modifier =
                                    Modifier
                                        .size(ToggleButtonDefaults.IconSize)
                                        .wrapContentSize(align = Alignment.Center),
                            )
                            AnimatedVisibility(
                                visible = showFullToggleButtonContent,
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier.padding(start = 8.dp),
                                        text = stringResource(R.string.task_schedule_picker_dialog_notification)
                                    )
                                }
                            }
                        }
                    }
                    item {

                        ToggleButton(
                            checked = pickedDuration != null,
                            enabled = pickedDate != null && pickedTime != null,
                            onCheckedChange = {

                                isDurationPickerVisible = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AvTimer,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                            AnimatedVisibility(
                                visible = showFullToggleButtonContent,
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        text = pickedDuration?.toText()
                                            ?: stringResource(R.string.task_schedule_picker_dialog_pick_a_duration)
                                    )
                                    if (pickedDuration != null) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.clickable {
                                                onAction(
                                                    TaskSchedulePickerDialogUiAction
                                                        .OnPickDuration(null)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {

                        ToggleButton(
                            checked = isFullDay,
                            enabled = pickedDate != null,
                            onCheckedChange = {
                                onAction(
                                    TaskSchedulePickerDialogUiAction
                                        .OnToggleIsFullDay
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WbSunny,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                            AnimatedVisibility(
                                visible = showFullToggleButtonContent,
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        text = stringResource(R.string.task_schedule_picker_dialog_pick_full_day)
                                    )
                                }
                            }
                        }
                    }
                }
                CalendarItemsRow(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    FilledTonalButton(

                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.4f
                            ),
                            disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.7f),
                        ),
                        onClick = {
                            onAction(
                                TaskSchedulePickerDialogUiAction
                                    .OnSaveTaskSchedule
                            )
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.time_picker_dialog_save_text)
                        )
                    }
                }
            }
        }

    }


}


@Composable
fun Modifier.itemCircle(divider: Int = 2, color: Color) = drawBehind {
    drawCircle(
        color = color,
        radius = size.minDimension.div(divider),
    )
}