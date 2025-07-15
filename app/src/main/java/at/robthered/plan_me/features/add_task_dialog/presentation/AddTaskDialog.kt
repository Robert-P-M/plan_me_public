package at.robthered.plan_me.features.add_task_dialog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.AddTaskDialogNavigationActions
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.AddTaskDialogNavigationEvent
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.icons.Hashtag
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.presentation.ext.model.imageVector
import at.robthered.plan_me.features.data_source.presentation.ext.model.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.inbox_screen.presentation.composables.utils.taskCardItemPriorityGradient
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toDurationEnd
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toFullDateFormat
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toLocalDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toText
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toTimeOfDay
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTaskDialogRoot(
    modifier: Modifier = Modifier,
    addTaskDialogViewModel: AddTaskDialogViewModel = koinViewModel<AddTaskDialogViewModel>(),
    addTaskDialogNavigationActions: AddTaskDialogNavigationActions,
) {

    val addTaskModel by addTaskDialogViewModel.addTaskModel.collectAsState()
    val addTaskModelError by addTaskDialogViewModel.addTaskModelError.collectAsState()
    val didModelChange by addTaskDialogViewModel.didModelChange.collectAsState()
    val canSave by addTaskDialogViewModel.canSave.collectAsState()
    val isLoading by addTaskDialogViewModel.isLoading.collectAsState()



    ObserveAsEvents(addTaskDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            AddTaskDialogNavigationEvent.OnNavigateBack -> {
                addTaskDialogNavigationActions.onNavigateBack()
            }

            AddTaskDialogNavigationEvent.OnNavigateToPriorityPickerDialog -> {
                addTaskDialogNavigationActions.onNavigateToPriorityPickerDialog()
            }

            AddTaskDialogNavigationEvent.OnNavigateToHashtagPickerDialog -> {
                addTaskDialogNavigationActions.onNavigateToHashtagPickerDialog()
            }

            is AddTaskDialogNavigationEvent.OnNavigateToTaskSchedulePickerDialog -> {
                addTaskDialogNavigationActions
                    .onNavigateToTaskSchedulePickerDialog(args = event.args)
            }
        }
    }



    AddTaskDialog(
        modifier = modifier,
        addTaskModel = addTaskModel,
        addTaskModelError = addTaskModelError,
        didModelChange = didModelChange,
        canSave = canSave,
        isLoading = isLoading,
        onAction = addTaskDialogViewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddTaskDialog(
    modifier: Modifier = Modifier,
    addTaskModel: AddTaskModel,
    onAction: (action: AddTaskDialogAction) -> Unit,
    addTaskModelError: AddTaskModelError,
    didModelChange: Boolean,
    canSave: Boolean,
    isLoading: Boolean,
) {

    val (title, description) = remember { FocusRequester.createRefs() }
    val listState = rememberLazyListState()

    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didModelChange,
        onNavigateBack = {
            onAction(
                AddTaskDialogAction
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

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            appSheetState.requestHide()
        },
        sheetState = appSheetState.sheetState,
        dragHandle = null
    ) {
        LazyColumn(
            modifier = Modifier,
            state = listState,
        ) {
            item {
                AppModalTitle(
                    modifier = Modifier
                        .then(
                            if (addTaskModel.priorityEnum != null) {
                                Modifier.background(
                                    brush = taskCardItemPriorityGradient(
                                        priorityEnum = addTaskModel.priorityEnum,
                                        defaultBackgroundColor = BottomSheetDefaults.ContainerColor
                                    )
                                )
                            } else Modifier
                        )

                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    title = stringResource(R.string.add_task_dialog_modal_title),
                    leadingIcon = {
                        addTaskModel.priorityEnum?.let { priorityEnum ->
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterStart),
                                onClick = {},
                            ) {
                                Icon(
                                    imageVector = priorityEnum.imageVector(),
                                    contentDescription = priorityEnum.toUiText().asString()
                                )
                            }
                        }
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
            }
            item {
                AppOutlinedTextField(
                    modifier = Modifier
                        .focusRequester(title)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = addTaskModel.title,
                    onValueChange = {
                        onAction(
                            AddTaskDialogAction
                                .OnChangeTitle(it)
                        )
                    },
                    error = addTaskModelError.title?.toUiText(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            description.requestFocus()
                        }
                    ),
                    placeholder = stringResource(id = R.string.add_task_dialog_task_title_placeholder),
                    label = stringResource(R.string.add_task_dialog_task_title_label)
                )
            }
            item {
                AppOutlinedTextField(
                    modifier = Modifier
                        .focusRequester(description)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = addTaskModel.description ?: "",
                    onValueChange = {
                        if (it.isNotEmpty()) {
                            onAction(
                                AddTaskDialogAction
                                    .OnChangeDescription(it)
                            )
                        } else {
                            onAction(
                                AddTaskDialogAction
                                    .OnChangeDescription(null)
                            )
                        }

                    },
                    singleLine = false,
                    error = addTaskModelError.description?.toUiText(),
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions(),
                    placeholder = stringResource(id = R.string.add_task_dialog_task_description_placeholder),
                    label = stringResource(id = R.string.add_task_dialog_task_description_label),
                )
            }

            item {
                val rowState = rememberLazyListState()
                val flingBehaviour =
                    rememberSnapFlingBehavior(
                        lazyListState = rowState,
                        snapPosition = SnapPosition.Center
                    )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Options"
                    )
                    LazyRow(
                        state = rowState,
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        flingBehavior = flingBehaviour,
                    ) {
                        item {
                            ToggleButton(
                                modifier = Modifier,
                                checked = addTaskModel.priorityEnum != PriorityEnum.NORMAL,
                                onCheckedChange = {
                                    onAction(
                                        AddTaskDialogAction
                                            .OnNavigateToPriorityPickerDialog
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = addTaskModel.priorityEnum.imageVector(),
                                    contentDescription = addTaskModel.priorityEnum.toUiText(short = false)
                                        .asString(),
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = addTaskModel.priorityEnum.toUiText(short = false)
                                        .asString(),
                                )
                            }
                        }
                        item {
                            ToggleButton(
                                modifier = Modifier,
                                checked = addTaskModel.hashtags.isNotEmpty(),
                                onCheckedChange = {
                                    onAction(
                                        AddTaskDialogAction
                                            .OnNavigateToHashtagPickerDialog
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Hashtag,
                                    contentDescription = null,
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = stringResource(
                                        R.string.add_task_dialog_hashtags,
                                        addTaskModel.hashtags.size
                                    )
                                )
                            }
                        }
                        item {
                            ToggleButton(
                                modifier = Modifier,
                                checked = addTaskModel.taskSchedule != null,
                                onCheckedChange = {
                                    onAction(
                                        AddTaskDialogAction
                                            .OnNavigateToTaskSchedulePickerDialog(
                                                args = TaskSchedulePickerDialogArgs()
                                            )
                                    )
                                },
                            ) {
                                if (addTaskModel.taskSchedule?.isFullDay == true) {
                                    Icon(
                                        imageVector = Icons.Outlined.WbSunny,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(ToggleButtonDefaults.IconSize)
                                            .wrapContentSize(align = Alignment.Center)
                                            .padding(end = 8.dp),
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = addTaskModel.taskSchedule?.startDateInEpochDays?.toLocalDate()
                                        ?.toFullDateFormat() ?: stringResource(
                                        R.string.add_task_dialog_pick_date
                                    )
                                )
                                addTaskModel.taskSchedule?.timeOfDayInMinutes?.let { timeOfDayInMinutes ->
                                    Row(
                                        modifier = Modifier.padding(start = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        addTaskModel.taskSchedule.durationInMinutes?.let { durationInMinutes ->
                                            Text(
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 2.sp,
                                                text = timeOfDayInMinutes.toTimeOfDay()
                                                    .toText() + " - " + (timeOfDayInMinutes + durationInMinutes).toDurationEnd()
                                                    .toText()
                                            )
                                        } ?: Text(
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 2.sp,
                                            text = timeOfDayInMinutes.toTimeOfDay().toText()
                                        )

                                    }

                                }
                                if (addTaskModel.taskSchedule?.isNotificationEnabled == true) {
                                    Icon(
                                        imageVector = Icons.Outlined.Alarm,
                                        contentDescription = null,
                                        modifier =
                                            Modifier
                                                .size(ToggleButtonDefaults.IconSize)
                                                .wrapContentSize(align = Alignment.Center)
                                                .padding(start = 8.dp),
                                    )
                                }
                                if (addTaskModel.taskSchedule != null) {
                                    Icon(
                                        modifier = Modifier
                                            .size(ToggleButtonDefaults.IconSize)
                                            .wrapContentSize(align = Alignment.Center)
                                            .clickable {
                                                onAction(
                                                    AddTaskDialogAction.OnRemoveTaskScheduleEvent
                                                )
                                            },
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }

            }
            item {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        enabled = !isLoading,
                        onClick = {
                            appSheetState.requestHide()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.add_task_dialog_cancel_text)
                        )
                    }
                    AnimatedContent(targetState = isLoading) { loading ->
                        if (loading) {
                            CircularProgressIndicator()
                        } else {
                            FilledTonalIconButton(
                                modifier = Modifier
                                    .wrapContentSize(align = Alignment.Center),
                                enabled = !isLoading,
                                onClick = {
                                    onAction(
                                        AddTaskDialogAction
                                            .OnResetState
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = stringResource(R.string.add_task_dialog_reset_input_icon_description)
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        enabled = canSave && !isLoading,
                        onClick = {
                            onAction(
                                AddTaskDialogAction
                                    .OnSaveTask
                            )
                        }
                    ) {
                        Text(
                            text = if (isLoading)
                                stringResource(R.string.add_task_dialog_saving_text)
                            else
                                stringResource(R.string.add_task_dialog_save_text)
                        )
                    }
                }
            }
        }
    }
}