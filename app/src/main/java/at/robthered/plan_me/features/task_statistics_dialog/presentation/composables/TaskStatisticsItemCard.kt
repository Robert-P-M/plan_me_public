package at.robthered.plan_me.features.task_statistics_dialog.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.data_source.presentation.ext.model.deleteText
import at.robthered.plan_me.features.data_source.presentation.ext.model.iconTint
import at.robthered.plan_me.features.data_source.presentation.ext.model.imageVector
import at.robthered.plan_me.features.data_source.presentation.ext.model.onClick
import at.robthered.plan_me.features.data_source.presentation.ext.model.text
import at.robthered.plan_me.features.data_source.presentation.ext.model.titleText
import at.robthered.plan_me.features.data_source.presentation.ext.model.toCreatedAtAnnotatedString
import at.robthered.plan_me.features.data_source.presentation.ext.model.toUiTextAnnotated
import at.robthered.plan_me.features.inbox_screen.presentation.composables.TaskScheduleEventDetails
import at.robthered.plan_me.features.inbox_screen.presentation.composables.stampOverlay
import at.robthered.plan_me.features.task_statistics_dialog.presentation.TaskStatisticsDialogUiAction
import at.robthered.plan_me.features.ui.presentation.composables.AppDeleteDialog
import at.robthered.plan_me.features.ui.presentation.modifier.borderBottom

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskStatisticsItemCard(
    modifier: Modifier = Modifier,
    onAction: (action: TaskStatisticsDialogUiAction) -> Unit,
    isLoading: Boolean,
    taskStatisticsModel: TaskStatisticsModel,
) {
    val strokeColor = MaterialTheme.colorScheme.onSurfaceVariant
        .copy(alpha = 0.3f)

    var showConfirmDeleteTaskDialog by remember {
        mutableStateOf(false)
    }

    var itemToDelete by remember {
        mutableStateOf<TaskStatisticsModel?>(null)
    }

    if (showConfirmDeleteTaskDialog) {
        itemToDelete?.let { taskStatisticsItemToDelete ->
            val text = taskStatisticsItemToDelete.deleteText().asAnnotatedString(
                color = MaterialTheme.colorScheme.primary
            )
            AppDeleteDialog(
                modifier = Modifier,
                onDismissRequest = { showConfirmDeleteTaskDialog = false },
                onAccept = {
                    taskStatisticsItemToDelete.onClick(onAction)
                    showConfirmDeleteTaskDialog = false
                    itemToDelete = null
                },
                title = taskStatisticsItemToDelete.titleText().asString(),
                text = text
            )
        }
    }

    Row(
        modifier = modifier
            .borderBottom(strokeColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(ToggleButtonDefaults.IconSize)
                .wrapContentSize(align = Alignment.Center),
            imageVector = taskStatisticsModel.imageVector(),
            tint = taskStatisticsModel.iconTint(),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (taskStatisticsModel.isLast && taskStatisticsModel is TaskStatisticsModel.ScheduleEvent && !taskStatisticsModel.isActiveScheduleEvent) {
                        Modifier
                            .alpha(0.8f)
                            .stampOverlay(stampText = stringResource(R.string.task_schedule_item_not_active))
                    } else Modifier
                )
        ) {

            val text = when (taskStatisticsModel) {
                is TaskStatisticsModel.SubTask,
                is TaskStatisticsModel.TaskArchivedHistory,
                is TaskStatisticsModel.TaskCompletedHistory,
                is TaskStatisticsModel.TaskDescriptionHistory,
                is TaskStatisticsModel.TaskInfo,
                is TaskStatisticsModel.TaskPriorityHistory,
                is TaskStatisticsModel.TaskTitleHistory,
                is TaskStatisticsModel.ScheduleEvent,
                    -> taskStatisticsModel.text().asAnnotatedString(
                    textDecoration = if (
                        taskStatisticsModel is TaskStatisticsModel.SubTask
                        || taskStatisticsModel is TaskStatisticsModel.TaskPriorityHistory
                    ) {
                        TextDecoration.Underline
                    } else null
                )

                is TaskStatisticsModel.Hashtags -> taskStatisticsModel.hashtags.toUiTextAnnotated { model ->
                    onAction(
                        TaskStatisticsDialogUiAction
                            .OnNavigateToHashtagTasksDialog(
                                args = HashtagTasksDialogArgs(
                                    hashtagId = model.hashtagId
                                )
                            )
                    )
                }.asAnnotatedString()
            }
            Text(
                modifier = Modifier
                    .clickable(
                        enabled = taskStatisticsModel is TaskStatisticsModel.SubTask,
                        onClick = {
                            if (taskStatisticsModel is TaskStatisticsModel.SubTask) {
                                onAction(
                                    TaskStatisticsDialogUiAction
                                        .OnNavigateToTaskDetailsDialog(
                                            args = TaskDetailsDialogArgs(
                                                taskId = taskStatisticsModel.taskModel.taskId
                                            )
                                        )
                                )
                            }

                        }
                    ),
                text = text,
            )
            if (taskStatisticsModel is TaskStatisticsModel.ScheduleEvent) {
                TaskScheduleEventDetails(
                    modifier = Modifier
                        .clickable {
                            // TODO: navigate to schedule ?
                        },
                    showLeadingIcon = false,
                    taskScheduleEventModel = taskStatisticsModel.taskScheduleEventModel,
                )

            }
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = taskStatisticsModel.createdAt.toCreatedAtAnnotatedString()
            )
        }
        if (!taskStatisticsModel.isLast) {
            IconButton(
                enabled = !isLoading,
                onClick = {
                    itemToDelete = taskStatisticsModel
                    showConfirmDeleteTaskDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = if (taskStatisticsModel.isLast) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}