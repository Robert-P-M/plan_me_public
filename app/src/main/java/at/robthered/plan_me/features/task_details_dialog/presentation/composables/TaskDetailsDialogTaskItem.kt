package at.robthered.plan_me.features.task_details_dialog.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.icons.TaskDescription
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.inbox_screen.presentation.composables.rememberHeaderMenuState
import at.robthered.plan_me.features.inbox_screen.presentation.composables.stampOverlay
import at.robthered.plan_me.features.task_details_dialog.presentation.TaskDetailsDialogUiAction
import at.robthered.plan_me.features.ui.presentation.composables.AppDeleteDialog
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenu
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenuItem
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.MenuItem

@Composable
fun TaskDetailsDialogTaskItem(
    modifier: Modifier = Modifier,
    taskWithHashtagsAndChildrenModel: TaskWithHashtagsAndChildrenModel,
    onAction: (action: TaskDetailsDialogUiAction) -> Unit,
    isParent: Boolean,
) {

    val errorTextColor = MaterialTheme.colorScheme.onErrorContainer
    val errorIconTint = MaterialTheme.colorScheme.onErrorContainer
    val errorBackgroundColor = MaterialTheme.colorScheme.errorContainer
    var showConfirmDeleteSubTaskDialog by remember {
        mutableStateOf(false)
    }
    if (showConfirmDeleteSubTaskDialog) {
        val deleteTaskText = UiText
            .StringResource(
                id = R.string.delete_task_dialog_text,
                args = listOf(stringResource(R.string.delete_task_dialog_text_info))
            )
        AppDeleteDialog(
            modifier = Modifier,
            onDismissRequest = { showConfirmDeleteSubTaskDialog = false },
            onAccept = {
                onAction(
                    TaskDetailsDialogUiAction
                        .OnDeleteTask(
                            taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                            navigateBack = !isParent
                        )
                )
                showConfirmDeleteSubTaskDialog = false
            },
            title = stringResource(R.string.delete_task_dialog_title),
            text = deleteTaskText.asAnnotatedString(color = MaterialTheme.colorScheme.error)
        )
    }

    val subTaskHeaderMenu = rememberHeaderMenuState(
        createdAt = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.createdAt,
        additionalItems = {
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
                                        taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                    )
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
                        onAction(
                            TaskDetailsDialogUiAction
                                .OnToggleArchiveTask(
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                    title = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.title,
                                    isArchived = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived,
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
                        showConfirmDeleteSubTaskDialog = true
                        it.onHide()
                    },
                    textColor = errorTextColor,
                    iconTint = errorIconTint,
                    backgroundColor = errorBackgroundColor,
                )
            )
        }
    )
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
    Row(
        modifier = modifier
            .clickable(
                enabled = !isParent,
                onClick = {
                    onAction(
                        TaskDetailsDialogUiAction
                            .OnNavigateToTaskDetailsDialog(
                                taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                            )
                    )
                }
            )
            .then(
                if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isArchived) {
                    Modifier.stampOverlay()
                } else {
                    Modifier
                }
            )
            .alpha(cardAlpha)
            .height(IntrinsicSize.Min)
            .heightIn(min = 48.dp)
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier
                    .clickable {
                        onAction(
                            TaskDetailsDialogUiAction
                                .OnToggleCompleteTask(
                                    taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                    title = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.title,
                                    isCompleted = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted
                                )
                        )
                    },
                imageVector = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                contentDescription = if (taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.isCompleted) "Task is completed - Icon" else "Task is not completed - Icon",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.description?.let {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .clickable {
                            onAction(
                                TaskDetailsDialogUiAction
                                    .OnNavigateToUpdateTaskDialog(
                                        args = UpdateTaskDialogArgs(
                                            taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                        )
                                    )
                            )
                        },
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                text = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.description?.let {
                Text(
                    modifier = Modifier,
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } ?: run {
                Row(
                    modifier = Modifier
                        .clickable {
                            onAction(
                                TaskDetailsDialogUiAction
                                    .OnNavigateToUpdateTaskDialog(
                                        args = UpdateTaskDialogArgs(
                                            taskId = taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId
                                        )
                                    )
                            )
                        }
                ) {
                    Icon(
                        imageVector = TaskDescription,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                    Text(
                        text = stringResource(R.string.task_details_dialog_no_description),
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                }
            }
        }
        if (!isParent) {
            IconButton(
                onClick = { subTaskHeaderMenu.onShow() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreHoriz,
                    contentDescription = null
                )
            }
            AppDropdownMenu(
                expanded = subTaskHeaderMenu.isOpen,
                onDismissRequest = { subTaskHeaderMenu.onHide() }
            ) {
                subTaskHeaderMenu.menuItems.map { menuItem ->
                    AppDropdownMenuItem(
                        menuItem = menuItem
                    )
                }
            }
        }
    }
}