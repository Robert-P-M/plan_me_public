package at.robthered.plan_me.features.inbox_screen.presentation.composables

import android.annotation.SuppressLint
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.inbox_screen.presentation.InboxScreenUiAction
import at.robthered.plan_me.features.ui.presentation.composables.HeaderRow

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BoardViewContent(
    modifier: Modifier = Modifier,
    onAction: (InboxScreenUiAction) -> Unit,
    getIsTaskExpanded: (Long) -> Boolean,
    getIsSectionExpanded: (Long) -> Boolean,
    onToggleTask: (Long) -> Unit,
    onToggleSection: (Long) -> Unit,
    inboxScreenUiModels: List<InboxScreenUiModel>,
    onDeleteTask: (Long) -> Unit,
    onDuplicateTask: (taskId: Long) -> Unit,
    onDeleteSection: (Long) -> Unit,
    onToggleArchiveTask: (Long, String, Boolean) -> Unit,
    onNavigateToTaskHashtags: (args: TaskHashtagsDialogArgs) -> Unit,
) {
    val lazyRowState = rememberLazyListState()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val columnWidth = (screenWidth - 32.dp).coerceAtMost(360.dp)
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = lazyRowState,
        snapPosition = SnapPosition.Center
    )

    val hasTasksItem by remember(
        inboxScreenUiModels
    ) {
        derivedStateOf {
            inboxScreenUiModels.any { it is InboxScreenUiModel.Tasks }
        }
    }


    LazyRow(
        modifier = modifier.fillMaxSize(),
        state = lazyRowState,
        flingBehavior = flingBehavior,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {

        items(inboxScreenUiModels) { inboxScreenItem ->
            when (inboxScreenItem) {
                is InboxScreenUiModel.Tasks -> {
                    val tasksListState = rememberLazyListState()
                    val tasksListFlingBehaviour = rememberSnapFlingBehavior(
                        lazyListState = tasksListState,
                        snapPosition = SnapPosition.Center
                    )
                    LazyColumn(
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = tween(),
                                placementSpec = spring(),
                                fadeOutSpec = tween()
                            )
                            .width(columnWidth)
                            .fillMaxHeight(),
                        state = tasksListState,
                        flingBehavior = tasksListFlingBehaviour,
                    ) {
                        stickyHeader {
                            HeaderRow(
                                modifier = modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = RoundedCornerShape(size = 8.dp)
                                    ),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f),
                                    text = stringResource(R.string.board_view_no_section_header),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = inboxScreenItem.tasks.size.toString()
                                )
                            }
                        }
                        items(
                            items = inboxScreenItem.tasks,
                        ) { taskWithHashtagsAndChildrenModel: TaskWithHashtagsAndChildrenModel ->
                            val isTaskExpanded by remember(
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                getIsTaskExpanded
                            ) {
                                derivedStateOf {
                                    getIsTaskExpanded(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId)
                                }
                            }
                            TaskCardHeader(
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = tween(),
                                        placementSpec = spring(),
                                        fadeOutSpec = tween()
                                    ),
                                taskWithHashtagsAndChildrenModel = taskWithHashtagsAndChildrenModel,
                                onNavigateToAddTask = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToAddTaskDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToPriorityPicker = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToPriorityPickerDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToUpdateTaskDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToUpdateTaskDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToTaskStatisticsDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToTaskStatisticsDialog(
                                                args = args
                                            )
                                    )
                                },
                                onToggleChildren = { onToggleTask(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId) },
                                isTaskExpanded = isTaskExpanded,
                                onToggleCompleteTask = { taskId: Long, title: String, isCompleted: Boolean ->
                                    onAction(
                                        InboxScreenUiAction.OnToggleCompleteTask(
                                            taskId = taskId,
                                            title = title,
                                            isCompleted = isCompleted
                                        )
                                    )
                                },
                                onToggleArchiveTask = onToggleArchiveTask,
                                onDuplicateTask = onDuplicateTask,
                                depth = 0,
                                onDeleteTask = onDeleteTask,
                                onNavigateToToTaskDetailsDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToTaskDetailsDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToTaskHashtags = onNavigateToTaskHashtags,
                                onNavigateToMoveTaskDialog = { args: MoveTaskDialogArgs ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToMoveTaskDialog(
                                                args = args
                                            )
                                    )
                                }
                            )
                            if (taskWithHashtagsAndChildrenModel.children.isNotEmpty() && isTaskExpanded) {
                                ChildrenTasks(
                                    taskWithHashtagsAndChildrenModel = taskWithHashtagsAndChildrenModel.children,
                                    getIsTaskExpanded = getIsTaskExpanded,
                                    onNavigateToUpdateTaskDialog = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToUpdateTaskDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onToggleChildren = onToggleTask,
                                    onNavigateToTaskStatisticsDialog = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToTaskStatisticsDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onNavigateToAddTask = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToAddTaskDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onNavigateToPriorityPicker = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToPriorityPickerDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onToggleCompleteTask = { taskId: Long, title: String, isCompleted: Boolean ->
                                        onAction(
                                            InboxScreenUiAction.OnToggleCompleteTask(
                                                taskId = taskId,
                                                title = title,
                                                isCompleted = isCompleted
                                            )
                                        )
                                    },
                                    depth = 0,
                                    onDeleteTask = onDeleteTask,
                                    onNavigateToTaskDetailsDialog = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToTaskDetailsDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onDuplicateTask = onDuplicateTask,
                                    onToggleArchiveTask = onToggleArchiveTask,
                                    onNavigateToTaskHashtags = onNavigateToTaskHashtags,
                                    onNavigateToMoveTaskDialog = { args: MoveTaskDialogArgs ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToMoveTaskDialog(
                                                    args = args
                                                )
                                        )
                                    }
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(96.dp))
                        }
                    }
                }

                is InboxScreenUiModel.Section -> {
                    val tasksListState = rememberLazyListState()
                    val tasksListFlingBehaviour = rememberSnapFlingBehavior(
                        lazyListState = tasksListState,
                        snapPosition = SnapPosition.Center
                    )
                    LazyColumn(
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = tween(),
                                placementSpec = spring(),
                                fadeOutSpec = tween()
                            )
                            .width(columnWidth)
                            .fillMaxHeight(),
                        state = tasksListState,
                        flingBehavior = tasksListFlingBehaviour,
                    ) {
                        stickyHeader {
                            SectionCardHeader(
                                sectionModel = inboxScreenItem.section,
                                childrenCount = inboxScreenItem.tasks.size,
                                onToggleChildren = {
                                    onToggleSection(inboxScreenItem.section.sectionId)
                                },
                                onNavigateToAddTask = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToAddTaskDialog(
                                                args = args
                                            )
                                    )
                                },
                                isSectionExpanded = getIsSectionExpanded(inboxScreenItem.section.sectionId),
                                onDeleteSection = onDeleteSection,
                                onNavigateToUpdateSectionDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToUpdateSectionTitleDialog(
                                                args = args
                                            )
                                    )
                                },
                                showExpandIcon = false
                            )
                        }
                        items(
                            items = inboxScreenItem.tasks,
                        ) { taskWithHashtagsAndChildrenModel: TaskWithHashtagsAndChildrenModel ->
                            val isTaskExpanded by remember(
                                taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId,
                                getIsTaskExpanded
                            ) {
                                derivedStateOf {
                                    getIsTaskExpanded(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId)
                                }
                            }
                            TaskCardHeader(
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = tween(),
                                        placementSpec = spring(),
                                        fadeOutSpec = tween()
                                    ),
                                taskWithHashtagsAndChildrenModel = taskWithHashtagsAndChildrenModel,
                                onNavigateToAddTask = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToAddTaskDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToPriorityPicker = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToPriorityPickerDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToUpdateTaskDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToUpdateTaskDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToTaskStatisticsDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToTaskStatisticsDialog(
                                                args = args
                                            )
                                    )
                                },
                                onToggleChildren = { onToggleTask(taskWithHashtagsAndChildrenModel.taskWithUiHashtagsModel.task.taskId) },
                                isTaskExpanded = isTaskExpanded,
                                onToggleCompleteTask = { taskId: Long, title: String, isCompleted: Boolean ->
                                    onAction(
                                        InboxScreenUiAction.OnToggleCompleteTask(
                                            taskId = taskId,
                                            title = title,
                                            isCompleted = isCompleted
                                        )
                                    )
                                },
                                onToggleArchiveTask = onToggleArchiveTask,
                                onDuplicateTask = onDuplicateTask,
                                depth = 1,
                                onDeleteTask = onDeleteTask,
                                onNavigateToToTaskDetailsDialog = { args ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToTaskDetailsDialog(
                                                args = args
                                            )
                                    )
                                },
                                onNavigateToTaskHashtags = onNavigateToTaskHashtags,
                                onNavigateToMoveTaskDialog = { args: MoveTaskDialogArgs ->
                                    onAction(
                                        InboxScreenUiAction
                                            .OnNavigateToMoveTaskDialog(
                                                args = args
                                            )
                                    )
                                }
                            )
                            if (taskWithHashtagsAndChildrenModel.children.isNotEmpty() && isTaskExpanded) {
                                ChildrenTasks(
                                    taskWithHashtagsAndChildrenModel = taskWithHashtagsAndChildrenModel.children,
                                    getIsTaskExpanded = getIsTaskExpanded,
                                    onNavigateToUpdateTaskDialog = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToUpdateTaskDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onToggleChildren = onToggleTask,
                                    onNavigateToTaskStatisticsDialog = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToTaskStatisticsDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onNavigateToAddTask = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToAddTaskDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onNavigateToPriorityPicker = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToPriorityPickerDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onToggleCompleteTask = { taskId: Long, title: String, isCompleted: Boolean ->
                                        onAction(
                                            InboxScreenUiAction.OnToggleCompleteTask(
                                                taskId = taskId,
                                                title = title,
                                                isCompleted = isCompleted
                                            )
                                        )
                                    },
                                    depth = 1,
                                    onDeleteTask = onDeleteTask,
                                    onNavigateToTaskDetailsDialog = { args ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToTaskDetailsDialog(
                                                    args = args
                                                )
                                        )
                                    },
                                    onDuplicateTask = onDuplicateTask,
                                    onToggleArchiveTask = onToggleArchiveTask,
                                    onNavigateToTaskHashtags = onNavigateToTaskHashtags,
                                    onNavigateToMoveTaskDialog = { args: MoveTaskDialogArgs ->
                                        onAction(
                                            InboxScreenUiAction
                                                .OnNavigateToMoveTaskDialog(
                                                    args = args
                                                )
                                        )
                                    }
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(96.dp))
                        }
                    }
                }
            }
        }


        item {
            Column(
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = tween(),
                        placementSpec = spring(),
                        fadeOutSpec = tween()
                    )
                    .width(columnWidth)
            ) {
                HeaderRow(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        )
                        .clickable(onClick = {
                            onAction(
                                InboxScreenUiAction
                                    .OnNavigateToAddSectionDialog
                            )
                        }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.board_view_add_new_section_row)
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(R.string.board_view_content_add_section),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (!hasTasksItem) {
                    HeaderRow(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(
                                    topStart = 8.dp,
                                    topEnd = 8.dp,
                                    bottomStart = 8.dp,
                                    bottomEnd = 8.dp
                                )
                            )
                            .clickable(onClick = {
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToAddTaskDialog(
                                            args = AddTaskDialogArgs()
                                        )
                                )

                            }),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.board_view_add_new_task_row)
                        )
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = stringResource(R.string.board_view_add_new_task_row),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

            }
        }


    }
}