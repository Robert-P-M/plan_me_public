package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.SwipeRightAlt
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.icons.PriorityPicker
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.PriorityPickerUpdateArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.presentation.ext.model.borderBrush
import at.robthered.plan_me.features.data_source.presentation.ext.model.imageVector
import at.robthered.plan_me.features.data_source.presentation.ext.model.toUiText
import at.robthered.plan_me.features.inbox_screen.presentation.composables.utils.taskCardItemPriorityGradient
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toDurationEnd
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toFullDateFormat
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toLocalDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toText
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toTimeOfDay
import at.robthered.plan_me.features.ui.presentation.composables.AppDeleteDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppDialog
import at.robthered.plan_me.features.ui.presentation.composables.HeaderRow
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenu
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenuItem
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.MenuItem


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskCardHeader(
    modifier: Modifier = Modifier,
    taskWithHashtagsAndChildrenModel: TaskWithHashtagsAndChildrenModel,
    onNavigateToAddTask: (args: AddTaskDialogArgs) -> Unit,
    onNavigateToPriorityPicker: (args: PriorityPickerUpdateArgs) -> Unit,
    onNavigateToUpdateTaskDialog: (args: UpdateTaskDialogArgs) -> Unit,
    onNavigateToTaskStatisticsDialog: (args: TaskStatisticsDialogArgs) -> Unit,
    onToggleChildren: () -> Unit,
    isTaskExpanded: Boolean,
    onToggleCompleteTask: (taskId: Long, title: String, isCompleted: Boolean) -> Unit,
    onToggleArchiveTask: (taskId: Long, title: String, isArchived: Boolean) -> Unit,
    onDuplicateTask: (taskId: Long) -> Unit,
    depth: Int,
    onDeleteTask: (taskId: Long) -> Unit,
    onNavigateToToTaskDetailsDialog: (args: TaskDetailsDialogArgs) -> Unit,
    onNavigateToTaskHashtags: (args: TaskHashtagsDialogArgs) -> Unit,
    onNavigateToMoveTaskDialog: (MoveTaskDialogArgs) -> Unit,
) {

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
                onDeleteTask(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId)
                showConfirmDeleteTaskDialog = false
            },
            title = stringResource(R.string.delete_task_dialog_title),
            text = deleteTaskText.asAnnotatedString(color = MaterialTheme.colorScheme.error)
        )
    }

    Column(
        modifier = modifier
            .height(IntrinsicSize.Max),
    ) {
        val errorTextColor = MaterialTheme.colorScheme.onErrorContainer
        val errorIconTint = MaterialTheme.colorScheme.onErrorContainer
        val errorBackgroundColor = MaterialTheme.colorScheme.errorContainer
        val taskCardHeaderMenu = rememberHeaderMenuState(
            createdAt = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.createdAt,
            keys = arrayOf<Any?>(
                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.priorityEnum,
                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived,
                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted,

                ),
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
                            onNavigateToTaskStatisticsDialog(
                                TaskStatisticsDialogArgs(
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                )
                            )
                        }
                    )
                )
                if (depth >= 4) {
                    add(
                        MenuItem.Default(
                            text = UiText.StringResource(R.string.task_card_header_dropdown_menu_item_text_go_to_details),
                            leadingIcon = Icons.AutoMirrored.Filled.ArrowRight,
                            iconDescription = UiText.StringResource(R.string.task_card_header_dropdown_menu_item_text_go_to_details),
                            onClick = {
                                it.onHide()
                                onNavigateToToTaskDetailsDialog(
                                    TaskDetailsDialogArgs(
                                        taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                    )
                                )
                            }
                        )
                    )
                } else {
                    add(
                        MenuItem.Default(
                            text = UiText.StringResource(R.string.task_card_header_dropdown_menu_item_add_sub_task_text),
                            leadingIcon = Icons.Outlined.AddCircle,
                            iconDescription = UiText.StringResource(R.string.task_card_header_dropdown_menu_item_add_sub_task_icon_description),
                            onClick = {
                                it.onHide()
                                onNavigateToAddTask(
                                    AddTaskDialogArgs(
                                        parentTaskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                    )
                                )
                            },
                        )
                    )
                }
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
                            it.onHide()
                            onNavigateToMoveTaskDialog(
                                MoveTaskDialogArgs(
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                    parentTaskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.parentTaskId,
                                    sectionId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.sectionId,
                                )
                            )
                        }
                    )
                )
                add(
                    MenuItem.Default(
                        text = UiText.StringResource(
                            id = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived) {
                                R.string.task_card_header_dropdown_menu_item_un_archive_task
                            } else {
                                R.string.task_card_header_dropdown_menu_item_archive_task
                            }
                        ),
                        leadingIcon = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived) {
                            Icons.Outlined.Unarchive
                        } else {
                            Icons.Outlined.Archive
                        },
                        iconDescription = UiText.StringResource(
                            id = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived) {
                                R.string.task_card_header_dropdown_menu_item_un_archive_task
                            } else {
                                R.string.task_card_header_dropdown_menu_item_archive_task
                            }
                        ),
                        onClick = {
                            it.onHide()
                            onToggleArchiveTask(
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.title,
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived
                            )
                        }
                    )
                )
                add(
                    MenuItem.Default(
                        text = UiText.StringResource(
                            id = R.string.task_card_header_dropdown_menu_item_duplicate
                        ),
                        leadingIcon = Icons.Outlined.ContentCopy,
                        iconDescription = UiText
                            .StringResource(
                                id = R.string.task_card_header_dropdown_menu_item_duplicate
                            ),
                        onClick = {
                            onDuplicateTask(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId)
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
                            onNavigateToUpdateTaskDialog(
                                UpdateTaskDialogArgs(
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                )

                            )
                        }
                    )
                )
                add(
                    MenuItem.Action(
                        text = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.priorityEnum.toUiText(
                            short = false
                        ),
                        leadingIcon = PriorityPicker,
                        onClick = {
                            onNavigateToPriorityPicker(
                                PriorityPickerUpdateArgs(
                                    priorityEnum = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.priorityEnum,
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                )
                            )
                            it.onHide()
                        },
                        trailingIcon = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.priorityEnum.imageVector()
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
        val haptics = LocalHapticFeedback.current
        val cardAlpha by animateFloatAsState(
            targetValue = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted) {
                0.2f
            } else 1f,
            animationSpec = tween(durationMillis = 450),
            label = "${taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted}-card-alpha"
        )
        val crossStrokeColor by animateColorAsState(
            targetValue = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
            },
            label = "${taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted}-cross-stroke-color",
            animationSpec = tween(durationMillis = 450)
        )

        HeaderRow(
            modifier = Modifier
                .background(
                    brush = taskCardItemPriorityGradient(priorityEnum = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.priorityEnum),
                    shape = RoundedCornerShape(8.dp)
                )
                .then(
                    if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived) {
                        Modifier.stampOverlay()
                    } else {
                        Modifier
                    }
                )
                .alpha(cardAlpha)
                .combinedClickable(
                    onDoubleClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        taskCardHeaderMenu.onShow()
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        /*taskCardHeaderMenu.onShow()*/
                    },
                    onLongClickLabel = stringResource(R.string.task_card_header_dropdown_menu_long_press_open_menu),
                    onClick = {
                        onNavigateToToTaskDetailsDialog(
                            TaskDetailsDialogArgs(
                                taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                            )
                        )
                    }
                )
                .clip(shape = RoundedCornerShape(size = 8.dp))
                .border(
                    width = 2.dp,
                    brush = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.priorityEnum.borderBrush(),
                    shape = RoundedCornerShape(8.dp)
                )
                .drawBehind {
                    drawLine(
                        start = Offset(
                            x = 24f,
                            y = 12f,
                        ),
                        end = Offset(
                            x = this.size.width - 24f,
                            y = this.size.height - 10f,
                        ),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                        color = crossStrokeColor
                    )
                    drawLine(
                        start = Offset(
                            x = 24f,
                            y = this.size.height - 12f,
                        ),
                        end = Offset(
                            x = this.size.width - 24f,
                            y = 12f,
                        ),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                        color = crossStrokeColor
                    )
                },
            verticalAlignment = Alignment.Top
        ) {
            var isDescriptionCurrentlyExpanded by remember(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId) {
                mutableStateOf(false)
            }
            var descriptionIsActuallyOverflowing by remember(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.description) {
                mutableStateOf(false)
            }
            val showDescriptionExpandIcon =
                descriptionIsActuallyOverflowing || isDescriptionCurrentlyExpanded

            var isDescriptionExpanded by remember(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId) {
                mutableStateOf(false)
            }


            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            onToggleCompleteTask(
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.title,
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted
                            )
                        },
                    imageVector = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                    contentDescription = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted) "Task is completed - Icon" else "Task is not completed - Icon",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (showDescriptionExpandIcon) {
                    Spacer(modifier = Modifier.weight(1f))
                    val rotationDegreeDescription by animateFloatAsState(
                        targetValue = if (isDescriptionCurrentlyExpanded) 180f else 0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "DescriptionExpandIconRotation"
                    )
                    Icon(
                        modifier = Modifier
                            .rotate(rotationDegreeDescription)
                            .clickable {
                                isDescriptionCurrentlyExpanded = !isDescriptionCurrentlyExpanded
                            },
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = if (isDescriptionCurrentlyExpanded) "Collapse description" else "Expand description",
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.description?.let {
                    Text(
                        modifier = Modifier,
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (isDescriptionCurrentlyExpanded) Int.MAX_VALUE else 2,
                        overflow = if (isDescriptionExpanded) {
                            TextOverflow.Visible
                        } else {
                            TextOverflow.Ellipsis
                        },
                        onTextLayout = { textLayoutResult ->
                            if (!isDescriptionCurrentlyExpanded) {
                                descriptionIsActuallyOverflowing =
                                    textLayoutResult.hasVisualOverflow
                            }
                        }
                    )
                }
                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskSchedule?.let { taskScheduleEventModel: TaskScheduleEventModel ->
                    TaskScheduleEventDetails(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        taskScheduleEventModel = taskScheduleEventModel
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box() {

                    AppDropdownMenu(
                        expanded = taskCardHeaderMenu.isOpen,
                        onDismissRequest = { taskCardHeaderMenu.onHide() }
                    ) {
                        taskCardHeaderMenu.menuItems.map { menuItem ->
                            AppDropdownMenuItem(
                                menuItem = menuItem
                            )
                        }
                    }


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (taskWithHashtagsAndChildrenModel.children.isNotEmpty()) {
                            Text(
                                text = taskWithHashtagsAndChildrenModel.children.size.toString(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            if (depth >= 4) {
                                Icon(
                                    modifier = Modifier
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                onNavigateToToTaskDetailsDialog(
                                                    TaskDetailsDialogArgs(
                                                        taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                                    )
                                                )
                                            }
                                        ),
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                    contentDescription = UiText.StringResource(R.string.task_card_header_dropdown_menu_item_text_go_to_details)
                                        .asString(),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            } else {
                                ChildrenToggleButton(
                                    isExpanded = isTaskExpanded,
                                    onToggle = onToggleChildren
                                )

                            }
                        }
                        Icon(
                            modifier = Modifier
                                .clickable(
                                    enabled = true,
                                    onClick = { taskCardHeaderMenu.onShow() }
                                ),
                            imageVector = Icons.Outlined.MoreHoriz,
                            contentDescription = "Section Menu",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                    }
                }

                var isHashtagsDialogVisible by remember {
                    mutableStateOf(false)
                }

                if (isHashtagsDialogVisible) {

                    AppDialog(
                        onDismissRequest = { isHashtagsDialogVisible = false }
                    ) {
                        Column {
                            taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.hashtags.map { hashtag ->
                                when (hashtag) {
                                    is UiHashtagModel.ExistingHashtagModel -> {
                                        Text(
                                            text = hashtag.name
                                        )
                                    }

                                    is UiHashtagModel.NewHashTagModel -> Unit
                                    is UiHashtagModel.FoundHashtagModel -> Unit
                                }
                            }
                        }
                    }
                }
                Icon(
                    modifier = Modifier
                        .clickable(
                            enabled = true
                        ) {
                            onNavigateToTaskHashtags(
                                TaskHashtagsDialogArgs(
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                )
                            )
                        }
                        .size(20.dp),
                    imageVector = Icons.Outlined.Tag,
                    contentDescription = "Task Hashtags - Icon",
                    tint = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.hashtags.isNotEmpty())
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.8f
                        ) else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}


@Composable
fun Modifier.stampOverlay(
    stampText: String = stringResource(id = R.string.task_archived),
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    color: Color = Color.Red.copy(alpha = 0.6f),
    angle: Float = -20f,
): Modifier {
    return drawWithContent {
        drawContent()
        val textLayoutResult = textMeasurer.measure(
            AnnotatedString(stampText),
            style = TextStyle(
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        )

        val textWidth = textLayoutResult.size.width
        val textHeight = textLayoutResult.size.height

        val centerX = size.width / 2f
        val centerY = size.height / 2f

        withTransform({
            rotate(degrees = angle, pivot = Offset(centerX, centerY))
        }) {
            drawText(
                textLayoutResult,
                topLeft = Offset(centerX - textWidth / 2f, centerY - textHeight / 2f)
            )
        }
    }
}

@Composable
fun TaskScheduleEventDetails(
    modifier: Modifier = Modifier,
    taskScheduleEventModel: TaskScheduleEventModel,
    showLeadingIcon: Boolean = true,
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (taskScheduleEventModel.isFullDay) {

            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Outlined.WbSunny,
                contentDescription = null,
            )
        }
        if (showLeadingIcon) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
            )
        }
        Text(
            modifier = Modifier,
            style = MaterialTheme.typography.bodySmall,
            text = taskScheduleEventModel.startDateInEpochDays.toLocalDate().toFullDateFormat()
        )

        taskScheduleEventModel.timeOfDayInMinutes?.let { timeOfDayInMinutes ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                taskScheduleEventModel.durationInMinutes?.let { durationInMinutes ->
                    Text(
                        style = MaterialTheme.typography.bodySmall,
                        text = timeOfDayInMinutes.toTimeOfDay()
                            .toText() + " - " + (timeOfDayInMinutes + durationInMinutes).toDurationEnd()
                            .toText()
                    )
                } ?: Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = timeOfDayInMinutes.toTimeOfDay().toText()
                )

            }

        }
        if (taskScheduleEventModel.isNotificationEnabled) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Outlined.Alarm,
                contentDescription = null,
            )
        }
    }

}