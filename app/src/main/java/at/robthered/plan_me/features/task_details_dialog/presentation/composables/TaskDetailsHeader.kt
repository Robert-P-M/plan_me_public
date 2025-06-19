package at.robthered.plan_me.features.task_details_dialog.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.icons.SectionIcon
import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModel
import at.robthered.plan_me.features.task_details_dialog.presentation.TaskDetailsDialogUiAction
import at.robthered.plan_me.features.task_details_dialog.presentation.ext.imageVector
import at.robthered.plan_me.features.task_details_dialog.presentation.ext.toUiText
import at.robthered.plan_me.features.ui.presentation.modifier.borderBottom

@Composable
fun TaskDetailsHeader(
    modifier: Modifier = Modifier,
    taskRoots: List<TaskTreeModel>,
    currentTaskId: Long,
    onAction: (TaskDetailsDialogUiAction) -> Unit,
    onOpenMenu: () -> Unit,
    onCloseModal: () -> Unit,
    menuContent: (@Composable BoxScope.() -> Unit) = {},
) {
    Row(
        modifier = modifier
            .borderBottom(
                strokeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        taskRoots.forEachIndexed { index, taskRoot ->
            when (taskRoot) {
                is TaskTreeModel.Root -> {
                    Row(
                        modifier = Modifier
                            .clickable {
                                onAction(
                                    TaskDetailsDialogUiAction
                                        .OnNavigateBack
                                )
                            },
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = taskRoot.taskTreeModelRootEnum.imageVector(),
                            contentDescription = null
                        )
                        Text(
                            text = taskRoot.taskTreeModelRootEnum.toUiText().asString()
                        )
                        Text(text = " / ")
                    }
                }

                is TaskTreeModel.Section,
                is TaskTreeModel.Task,
                    -> Unit
            }
        }

        val lazyRowState = rememberLazyListState()
        LaunchedEffect(taskRoots) {
            if (taskRoots.isNotEmpty()) {
                val lastIndex = taskRoots.lastIndex
                lazyRowState.animateScrollToItem(index = lastIndex)
            }
        }
        LazyRow(
            modifier = Modifier
                .weight(1f),
            state = lazyRowState,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            itemsIndexed(
                items = taskRoots
            ) { index, taskRoot ->
                Row(
                    modifier = Modifier
                        .clickable {
                            when (taskRoot) {
                                is TaskTreeModel.Root -> Unit
                                is TaskTreeModel.Section -> {
                                    onAction(
                                        TaskDetailsDialogUiAction
                                            .OnNavigateToSectionDetailsDialog(
                                                taskRoot.sectionId
                                            )
                                    )
                                }

                                is TaskTreeModel.Task -> {
                                    if (currentTaskId != taskRoot.taskId) {
                                        onAction(
                                            TaskDetailsDialogUiAction
                                                .OnNavigateToTaskDetailsDialog(
                                                    taskRoot.taskId
                                                )
                                        )
                                    }
                                }
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (taskRoot) {
                        is TaskTreeModel.Root -> Unit
                        is TaskTreeModel.Section -> {
                            Icon(
                                imageVector = SectionIcon,
                                contentDescription = null
                            )
                            Text(text = taskRoot.title)
                        }

                        is TaskTreeModel.Task -> {
                            Text(
                                text = taskRoot.title,
                                color = if (currentTaskId == taskRoot.taskId) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                textDecoration = if (currentTaskId == taskRoot.taskId) {
                                    TextDecoration.Underline
                                } else {
                                    TextDecoration.None
                                }
                            )
                        }
                    }
                    if (index != taskRoots.lastIndex && index != 0) {
                        Text(text = " / ")
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(
                    onClick = onOpenMenu
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = null
                    )
                }
                menuContent()
            }
            IconButton(
                onClick = onCloseModal,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null
                )
            }
        }
    }
}