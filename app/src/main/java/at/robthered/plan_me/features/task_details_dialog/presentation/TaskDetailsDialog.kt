package at.robthered.plan_me.features.task_details_dialog.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.NotInterested
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.SwipeRightAlt
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModel
import at.robthered.plan_me.features.data_source.presentation.ext.model.iconTint
import at.robthered.plan_me.features.data_source.presentation.ext.model.imageVector
import at.robthered.plan_me.features.data_source.presentation.ext.model.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.toUiText
import at.robthered.plan_me.features.inbox_screen.presentation.composables.TaskScheduleEventDetails
import at.robthered.plan_me.features.inbox_screen.presentation.composables.rememberHeaderMenuState
import at.robthered.plan_me.features.inbox_screen.presentation.composables.utils.taskCardItemPriorityGradient
import at.robthered.plan_me.features.task_details_dialog.presentation.composables.TaskDetailsDialogTaskItem
import at.robthered.plan_me.features.task_details_dialog.presentation.composables.TaskDetailsHeader
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.TaskDetailsDialogNavigationActions
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.TaskDetailsDialogNavigationEvent
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagCardItem
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagCardItemVisibility
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagItemActionIcon
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagsFlowRow
import at.robthered.plan_me.features.ui.presentation.composables.AppDeleteDialog
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenu
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenuItem
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.MenuItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskDetailsDialogRoot(
    modifier: Modifier = Modifier,
    taskDetailsDialogNavigationActions: TaskDetailsDialogNavigationActions,
    taskDetailsDialogViewModel: TaskDetailsDialogViewModel = koinViewModel<TaskDetailsDialogViewModel>(),
) {

    val taskDetailsResource by taskDetailsDialogViewModel.taskDetailsResource.collectAsStateWithLifecycle()
    val taskRoots by taskDetailsDialogViewModel.taskRoots.collectAsStateWithLifecycle()
    val isLoading by taskDetailsDialogViewModel.isLoading.collectAsStateWithLifecycle()
    val showArchived by taskDetailsDialogViewModel.showArchived.collectAsStateWithLifecycle()
    val showCompleted by taskDetailsDialogViewModel.showCompleted.collectAsStateWithLifecycle()
    val sortDirection by taskDetailsDialogViewModel.sortDirection.collectAsStateWithLifecycle()

    ObserveAsEvents(taskDetailsDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            TaskDetailsDialogNavigationEvent.OnNavigateBack -> {
                taskDetailsDialogNavigationActions.onNavigateBack()
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToAddTaskDialog -> {
                taskDetailsDialogNavigationActions
                    .onNavigateToAddTaskDialog(
                        args = event.args,
                    )
            }

            TaskDetailsDialogNavigationEvent.OnNavigateToPriorityPickerDialog -> {
                taskDetailsDialogNavigationActions.onNavigateToPriorityPickerDialog()
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToSectionDetailsDialog -> {
                taskDetailsDialogNavigationActions.onNavigateToSectionDetails(event.sectionId)
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToTaskDetailsDialogDialog -> {
                taskDetailsDialogNavigationActions.onNavigateToTaskDetailsDialog(event.taskId)
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToUpdateTaskDialog -> {
                taskDetailsDialogNavigationActions.onNavigateToUpdateTaskDialog(
                    args = event.args
                )
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToTaskStatisticsDialog -> {
                taskDetailsDialogNavigationActions.onNavigateToTaskStatisticsDialog(
                    args = event.args
                )
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToUpdateHashtagNameDialog -> {
                taskDetailsDialogNavigationActions
                    .onNavigateToUpdateHashtagNameDialog(
                        args = event.args
                    )
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToTaskHashtagsDialog -> {
                taskDetailsDialogNavigationActions
                    .onNavigateToTaskHashtagsDialog(
                        args = event.args
                    )
            }

            is TaskDetailsDialogNavigationEvent.OnNavigateToHashtagTasksDialog ->
                taskDetailsDialogNavigationActions
                    .onNavigateToHashtagTasksDialog(
                        args = event.args
                    )

            is TaskDetailsDialogNavigationEvent.OnNavigateToMoveTaskDialog ->
                taskDetailsDialogNavigationActions
                    .onNavigateToMoveTaskDialog(
                        args = event.args
                    )

            is TaskDetailsDialogNavigationEvent.OnNavigateToTaskScheduleEventDialog ->
                taskDetailsDialogNavigationActions
                    .onNavigateToTaskScheduleEventDialog(
                        args = event.args
                    )
        }
    }


    TaskDetailsDialog(
        modifier = modifier,
        taskDetailsResource = taskDetailsResource,
        taskRoots = taskRoots,
        isLoading = isLoading,
        onAction = taskDetailsDialogViewModel::onAction
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailsDialog(
    modifier: Modifier = Modifier,
    taskRoots: List<TaskTreeModel>,
    onAction: (TaskDetailsDialogUiAction) -> Unit,
    isLoading: Boolean,
    taskDetailsResource: AppResource<TaskWithHashtagsAndChildrenModel>,
) {


    val errorTextColor = MaterialTheme.colorScheme.onErrorContainer
    val errorIconTint = MaterialTheme.colorScheme.onErrorContainer
    val errorBackgroundColor = MaterialTheme.colorScheme.errorContainer

    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = false,
        onNavigateBack = {
            onAction(
                TaskDetailsDialogUiAction
                    .OnNavigateBack
            )
        }
    )

    val modalScrollableState = rememberScrollableState(
        consumeScrollDelta = { 1f }
    )

    val localConfiguration = LocalConfiguration.current
    val screenHeight by remember {
        derivedStateOf {
            localConfiguration.screenHeightDp.dp.div(3).times(2)
        }
    }


    ModalBottomSheet(
        modifier = modifier.scrollable(modalScrollableState, orientation = Orientation.Vertical),
        sheetState = appSheetState.sheetState,
        onDismissRequest = {
            appSheetState.requestHide()
        },
        dragHandle = null,
    ) {

        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = taskDetailsResource is AppResource.Loading || isLoading,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        when (val taskDetails = taskDetailsResource) {
            is AppResource.Error -> {
                when (taskDetails.error is RoomDatabaseError) {
                    true -> {
                        Text(
                            text = taskDetails.error.toUiText().asString()
                        )
                    }

                    false -> {
                        Text(
                            text = "An unknown error occures"
                        )
                    }
                }
            }

            is AppResource.Loading,
            AppResource.Stale,
                -> Unit

            is AppResource.Success -> {
                val task: TaskWithHashtagsAndChildrenModel = taskDetails.data
                var showConfirmDeleteTaskDialog by remember {
                    mutableStateOf(false)
                }
                if (showConfirmDeleteTaskDialog) {
                    val deleteTaskText = UiText
                        .StringResource(
                            id = R.string.delete_task_dialog_text,
                            args = listOf(stringResource(R.string.delete_task_dialog_text_info))
                        )
                    AppDeleteDialog(
                        modifier = Modifier,
                        onDismissRequest = { showConfirmDeleteTaskDialog = false },
                        onAccept = {

                            onAction(
                                TaskDetailsDialogUiAction
                                    .OnDeleteTask(
                                        taskId = task.taskWithUiHashtagsModel.task.taskId
                                    )
                            )
                            showConfirmDeleteTaskDialog = false
                        },
                        title = stringResource(R.string.delete_task_dialog_title),
                        text = deleteTaskText.asAnnotatedString(color = MaterialTheme.colorScheme.error)
                    )
                }

                val listState = rememberLazyListState()
                var showSubtasks by remember {
                    mutableStateOf(false)
                }
                val rotationDegree by animateFloatAsState(
                    targetValue = if (showSubtasks) -180f else 0f,
                    animationSpec = tween(durationMillis = 200),
                    label = "Expand/Collapse Icon Rotation",
                )
                LazyColumn(
                    modifier = Modifier,
                    state = listState,
                ) {
                    item {
                        val taskDetailsHeaderMenu = rememberHeaderMenuState(
                            createdAt = task.taskWithUiHashtagsModel.task.createdAt,
                            keys = arrayOf(task.taskWithUiHashtagsModel.task),
                            additionalItems = {
                                add(
                                    MenuItem.Action(
                                        text = UiText.StringResource(
                                            id = R.string.task_details_dialog_dropdown_menu_item_show_statistic_text
                                        ),
                                        leadingIcon = Icons.Outlined.AutoGraph,
                                        iconDescription = UiText.StringResource(
                                            id = R.string.task_details_dialog_dropdown_menu_item_show_statistic_text
                                        ),
                                        onClick = {
                                            it.onHide()
                                            onAction(
                                                TaskDetailsDialogUiAction
                                                    .OnNavigateToTaskStatisticsDialog(
                                                        args = TaskStatisticsDialogArgs(
                                                            taskId = task.taskWithUiHashtagsModel.task.taskId
                                                        )
                                                    )
                                            )
                                        }
                                    )
                                )
                                add(
                                    MenuItem.Default(
                                        text = UiText.StringResource(
                                            id = R.string.task_card_header_dropdown_menu_item_move
                                        ),
                                        leadingIcon = Icons.Outlined.SwipeRightAlt,
                                        iconDescription = UiText.StringResource(
                                            id = R.string.task_card_header_dropdown_menu_item_move
                                        ),
                                        onClick = {
                                            onAction(
                                                TaskDetailsDialogUiAction
                                                    .OnNavigateToMoveTaskDialog(
                                                        args = MoveTaskDialogArgs(
                                                            taskId = task.taskWithUiHashtagsModel.task.taskId,
                                                            parentTaskId = task.taskWithUiHashtagsModel.task.parentTaskId,
                                                            sectionId = task.taskWithUiHashtagsModel.task.sectionId,
                                                        )
                                                    )
                                            )
                                            it.onHide()
                                        }
                                    )
                                )
                                add(
                                    MenuItem.Default(
                                        text = UiText.StringResource(
                                            id = if (task.taskWithUiHashtagsModel.task.isArchived) {
                                                R.string.task_card_header_dropdown_menu_item_un_archive_task
                                            } else {
                                                R.string.task_card_header_dropdown_menu_item_archive_task
                                            }
                                        ),
                                        leadingIcon = if (task.taskWithUiHashtagsModel.task.isArchived) {
                                            Icons.Outlined.Unarchive
                                        } else {
                                            Icons.Outlined.Archive
                                        },
                                        iconDescription = UiText.StringResource(
                                            id = if (task.taskWithUiHashtagsModel.task.isArchived) {
                                                R.string.task_card_header_dropdown_menu_item_un_archive_task
                                            } else {
                                                R.string.task_card_header_dropdown_menu_item_archive_task
                                            }
                                        ),
                                        onClick = {
                                            it.onHide()
                                            onAction(
                                                TaskDetailsDialogUiAction
                                                    .OnToggleArchiveTask(
                                                        taskId = task.taskWithUiHashtagsModel.task.taskId,
                                                        title = task.taskWithUiHashtagsModel.task.title,
                                                        isArchived = task.taskWithUiHashtagsModel.task.isArchived,
                                                    )
                                            )
                                        }
                                    )
                                )
                                add(
                                    MenuItem.Action(
                                        text = UiText.StringResource(
                                            id = R.string.task_card_header_dropdown_menu_item_update_task_text,
                                        ),
                                        leadingIcon = Icons.Outlined.Edit,
                                        iconDescription = UiText.StringResource(
                                            id = R.string.task_card_header_dropdown_menu_item_update_task_text,
                                        ),
                                        onClick = {
                                            it.onHide()
                                            onAction(
                                                TaskDetailsDialogUiAction
                                                    .OnNavigateToUpdateTaskDialog(
                                                        args = UpdateTaskDialogArgs(
                                                            taskId = task.taskWithUiHashtagsModel.task.taskId
                                                        )
                                                    )
                                            )
                                        }
                                    )
                                )
                                add(
                                    MenuItem.Action(
                                        text = UiText.StringResource(
                                            id = R.string.task_card_header_dropdown_menu_item_delete_task_text,
                                        ),
                                        leadingIcon = Icons.Outlined.Delete,
                                        iconDescription = UiText.StringResource(
                                            id = R.string.task_card_header_dropdown_menu_item_delete_task_text,
                                        ),
                                        onClick = {
                                            showConfirmDeleteTaskDialog = true
                                            it.onHide()
                                        },
                                        textColor = errorTextColor,
                                        iconTint = errorIconTint,
                                        backgroundColor = errorBackgroundColor,
                                    )
                                )
                            }
                        )
                        TaskDetailsHeader(
                            modifier = Modifier
                                .background(
                                    brush = taskCardItemPriorityGradient(priorityEnum = task.taskWithUiHashtagsModel.task.priorityEnum)
                                ),
                            taskRoots = taskRoots,
                            onAction = onAction,
                            currentTaskId = task.taskWithUiHashtagsModel.task.taskId,
                            onCloseModal = {
                                appSheetState.requestHide()
                            },
                            onOpenMenu = { taskDetailsHeaderMenu.onShow() },
                            menuContent = {


                                AppDropdownMenu(
                                    expanded = taskDetailsHeaderMenu.isOpen,
                                    onDismissRequest = { taskDetailsHeaderMenu.onHide() }
                                ) {
                                    taskDetailsHeaderMenu.menuItems.map { menuItem ->
                                        AppDropdownMenuItem(
                                            menuItem = menuItem
                                        )
                                    }
                                }
                            }
                        )
                    }
                    item {
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxWidth(),
                            visible = isLoading,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    item {
                        TaskDetailsDialogTaskItem(
                            modifier = Modifier,
                            taskWithHashtagsAndChildrenModel = task,
                            onAction = onAction,
                            isParent = true,
                        )
                    }
                    horizontalDivider()
                    item {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .heightIn(min = 48.dp)
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    onAction(
                                        TaskDetailsDialogUiAction
                                            .OnNavigateToTaskScheduleEventPickerDialog(
                                                args = TaskSchedulePickerDialogArgs(
                                                    taskId = task.taskWithUiHashtagsModel.task.taskId,
                                                    startDateInEpochDays = task.taskWithUiHashtagsModel.task.taskSchedule?.startDateInEpochDays,
                                                    timeOfDayInMinutes = task.taskWithUiHashtagsModel.task.taskSchedule?.timeOfDayInMinutes,
                                                    isNotificationEnabled = task.taskWithUiHashtagsModel.task.taskSchedule?.isNotificationEnabled
                                                        ?: false,
                                                    durationInMinutes = task.taskWithUiHashtagsModel.task.taskSchedule?.durationInMinutes,
                                                    isFullDay = task.taskWithUiHashtagsModel.task.taskSchedule?.isFullDay
                                                        ?: false
                                                )
                                            )
                                    )
                                },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Today,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            task.taskWithUiHashtagsModel.task.taskSchedule?.let { taskScheduleEventModel ->
                                TaskScheduleEventDetails(
                                    showLeadingIcon = false,
                                    taskScheduleEventModel = taskScheduleEventModel,
                                )
                            } ?: Row(
                                modifier = modifier,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.task_details_dialog_no_date),
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                )
                            }

                        }
                    }
                    horizontalDivider()
                    item {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .heightIn(min = 48.dp)
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    onAction(
                                        TaskDetailsDialogUiAction
                                            .OnNavigateToPriorityPickerDialog(
                                                priorityEnum = task.taskWithUiHashtagsModel.task.priorityEnum,
                                                taskId = task.taskWithUiHashtagsModel.task.taskId
                                            )
                                    )
                                },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = task.taskWithUiHashtagsModel.task.priorityEnum.imageVector(),
                                contentDescription = task.taskWithUiHashtagsModel.task.priorityEnum.toUiText()
                                    .asString(),
                                tint = task.taskWithUiHashtagsModel.task.priorityEnum.iconTint()
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = task.taskWithUiHashtagsModel.task.priorityEnum.toUiText(short = false)
                                    .asString(),
                                textAlign = TextAlign.Start,
                                color = task.taskWithUiHashtagsModel.task.priorityEnum.iconTint()
                            )
                        }
                    }
                    horizontalDivider()
                    item {


                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .heightIn(min = 48.dp)
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clickable {
                                        onAction(
                                            TaskDetailsDialogUiAction
                                                .OnNavigateToTaskHashtagsDialog(
                                                    args = TaskHashtagsDialogArgs(
                                                        taskId = task.taskWithUiHashtagsModel.task.taskId
                                                    )
                                                )
                                        )
                                    },
                                imageVector = Icons.Outlined.Tag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            if (task.taskWithUiHashtagsModel.hashtags.isEmpty()) {
                                Row(
                                    modifier = modifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = stringResource(R.string.task_details_dialog_no_hashtags),
                                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clickable {
                                                onAction(
                                                    TaskDetailsDialogUiAction
                                                        .OnNavigateToTaskHashtagsDialog(
                                                            args = TaskHashtagsDialogArgs(
                                                                taskId = task.taskWithUiHashtagsModel.task.taskId
                                                            )
                                                        )
                                                )
                                            },
                                        imageVector = Icons.Outlined.AddCircleOutline,
                                        contentDescription = null
                                    )
                                }
                            }
                            HashtagsFlowRow(
                                hashtags = task.taskWithUiHashtagsModel.hashtags,
                                trailingContent = {

                                }
                            ) { index, uiHashtagModel ->
                                val haptics = LocalHapticFeedback.current
                                var isHashtagMenuOpen by remember {
                                    mutableStateOf(false)
                                }
                                val backgroundColor by animateColorAsState(
                                    targetValue = if (isHashtagMenuOpen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                                    animationSpec = tween(),
                                    label = "backgroundColor-id-${uiHashtagModel}"
                                )

                                HashtagCardItem(
                                    modifier = Modifier
                                        .combinedClickable(
                                            onDoubleClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                isHashtagMenuOpen = true
                                            },
                                            onLongClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                isHashtagMenuOpen = true
                                            },
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                isHashtagMenuOpen = true
                                            }
                                        ),
                                    backgroundColor = backgroundColor,
                                    text = when (uiHashtagModel) {
                                        is UiHashtagModel.ExistingHashtagModel -> {
                                            uiHashtagModel.name
                                        }

                                        is UiHashtagModel.NewHashTagModel -> {
                                            uiHashtagModel.name
                                        }

                                        is UiHashtagModel.FoundHashtagModel -> uiHashtagModel.name
                                    }
                                ) {
                                    HashtagCardItemVisibility(
                                        visible = isHashtagMenuOpen,
                                    ) {
                                        val hashtagId: Long? = when (uiHashtagModel) {
                                            is UiHashtagModel.ExistingHashtagModel -> uiHashtagModel.hashtagId
                                            is UiHashtagModel.FoundHashtagModel -> uiHashtagModel.hashtagId
                                            is UiHashtagModel.NewHashTagModel -> null
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            HashtagItemActionIcon(
                                                onClick = {
                                                    when (uiHashtagModel) {
                                                        is UiHashtagModel.ExistingHashtagModel -> {
                                                            onAction(
                                                                TaskDetailsDialogUiAction
                                                                    .OnNavigateToUpdateHashtagNameDialog(
                                                                        args = UpdateHashtagNameDialogArgs(
                                                                            hashtagId = uiHashtagModel.hashtagId
                                                                        )
                                                                    )
                                                            )
                                                        }

                                                        is UiHashtagModel.NewHashTagModel -> Unit
                                                        is UiHashtagModel.FoundHashtagModel -> Unit
                                                    }
                                                    isHashtagMenuOpen = false
                                                },
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = "Edit"
                                            )
                                            HashtagItemActionIcon(
                                                onClick = {
                                                    hashtagId?.let {
                                                        onAction(
                                                            TaskDetailsDialogUiAction
                                                                .OnNavigateToHashtagTasksDialog(
                                                                    args = HashtagTasksDialogArgs(
                                                                        hashtagId = it
                                                                    )
                                                                )
                                                        )
                                                    }
                                                    isHashtagMenuOpen = false
                                                },
                                                imageVector = Icons.Outlined.RemoveRedEye,
                                                contentDescription = "View"
                                            )
                                            HashtagItemActionIcon(
                                                onClick = {
                                                    hashtagId?.let {

                                                        onAction(
                                                            TaskDetailsDialogUiAction
                                                                .OnDeleteTaskHashtagReference(
                                                                    taskId = task.taskWithUiHashtagsModel.task.taskId,
                                                                    hashtagId = it,
                                                                )
                                                        )
                                                    }
                                                    isHashtagMenuOpen = false
                                                },
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = "Delete"
                                            )
                                            HashtagItemActionIcon(
                                                onClick = {
                                                    isHashtagMenuOpen = false
                                                },
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = "Close"
                                            )
                                        }
                                    }
                                }

                            }


                        }
                    }
                    horizontalDivider()
                    item {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .heightIn(min = 48.dp)
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            AnimatedContent(targetState = task.children.size) { size ->
                                when {
                                    size > 0 -> {
                                        Icon(
                                            modifier = Modifier
                                                .rotate(rotationDegree)
                                                .clickable {
                                                    showSubtasks = showSubtasks.not()
                                                },
                                            imageVector = Icons.Outlined.ExpandMore,
                                            contentDescription = null,
                                        )
                                    }

                                    else -> {
                                        Icon(
                                            modifier = Modifier.alpha(0.3f),
                                            imageVector = Icons.Outlined.NotInterested,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }

                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                text = pluralStringResource(
                                    id = R.plurals.task_details_dialog_subtasks,
                                    count = task.children.size,
                                    formatArgs = arrayOf(task.children.size)
                                )
                            )

                            Icon(
                                modifier = Modifier
                                    .clickable {
                                        onAction(
                                            TaskDetailsDialogUiAction
                                                .OnNavigateToAddTaskDialog(
                                                    AddTaskDialogArgs(
                                                        parentTaskId = task.taskWithUiHashtagsModel.task.taskId
                                                    )

                                                )
                                        )
                                    },
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null
                            )
                        }
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxWidth(),
                            visible = showSubtasks,
                            enter = fadeIn() + expandVertically(
                                animationSpec = tween(
                                    delayMillis = 100
                                )
                            ),
                            exit = fadeOut(
                                animationSpec = tween(
                                    delayMillis = 100
                                )
                            ) + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .heightIn(max = screenHeight)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                task.children.forEachIndexed { index, subTask ->


                                    TaskDetailsDialogTaskItem(
                                        modifier = Modifier.padding(start = 8.dp),
                                        taskWithHashtagsAndChildrenModel = subTask,
                                        onAction = onAction,
                                        isParent = false,
                                    )
                                }
                            }
                        }
                    }
                    horizontalDivider()



                    item {
                        Spacer(modifier = Modifier.height(96.dp))
                    }
                }
            }
        }


    }

}

fun LazyListScope.horizontalDivider() {
    item {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 32.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
        )
    }
}