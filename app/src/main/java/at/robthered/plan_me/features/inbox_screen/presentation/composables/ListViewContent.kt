package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.inbox_screen.presentation.InboxScreenUiAction
import at.robthered.plan_me.features.ui.presentation.composables.HeaderRow


@Composable
fun ListViewContent(
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
    val listState = rememberLazyListState()

    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState,
        snapPosition = SnapPosition.Center
    )

    val hasTasksItem by remember(
        inboxScreenUiModels
    ) {
        derivedStateOf {
            inboxScreenUiModels.any { it is InboxScreenUiModel.Tasks }
        }
    }
    val hasSectionItem by remember(
        inboxScreenUiModels
    ) {
        derivedStateOf {
            inboxScreenUiModels.any { it is InboxScreenUiModel.Section }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
        ),
        flingBehavior = flingBehavior,
    ) {
        if (!hasTasksItem && !hasSectionItem) {
            item {
                HeaderRow(
                    modifier = Modifier
                        .animateItem(
                            fadeInSpec = tween(),
                            placementSpec = spring(),
                            fadeOutSpec = tween()
                        ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        onClick = {
                            onAction(
                                InboxScreenUiAction
                                    .OnNavigateToAddTaskDialog(
                                        args = AddTaskDialogArgs()
                                    )
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.list_view_add_new_task)
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        onClick = {
                            onAction(
                                InboxScreenUiAction
                                    .OnNavigateToAddSectionDialog
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.list_view_add_new_section)
                        )
                    }
                }
            }
        }
        inboxScreenUiModels.mapIndexed { index: Int, inboxScreenModel: InboxScreenUiModel ->


            when (inboxScreenModel) {
                is InboxScreenUiModel.Tasks -> {
                    items(
                        items = inboxScreenModel.tasks,
                        key = { item: TaskWithHashtagsAndChildrenModel ->
                            item.taskWithUiHashtagsModel.task.taskId
                        }
                    ) { it ->
                        val isTaskExpanded by remember(
                            it.taskWithUiHashtagsModel.task.taskId,
                            getIsTaskExpanded
                        ) {
                            derivedStateOf {
                                getIsTaskExpanded(it.taskWithUiHashtagsModel.task.taskId)
                            }
                        }
                        TaskCardHeader(
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = tween(),
                                    placementSpec = spring(),
                                    fadeOutSpec = tween()
                                ),
                            taskWithHashtagsAndChildrenModel = it,
                            onNavigateToAddTask = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToAddTaskDialog(
                                            args = args
                                        )
                                )
                            },
                            onNavigateToMoveTaskDialog = { args: MoveTaskDialogArgs ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToMoveTaskDialog(
                                            args = args
                                        )
                                )
                            },
                            onToggleChildren = { onToggleTask(it.taskWithUiHashtagsModel.task.taskId) },
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
                            depth = 0,
                            onNavigateToUpdateTaskDialog = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToUpdateTaskDialog(
                                            args = args
                                        )
                                )
                            },
                            onNavigateToToTaskDetailsDialog = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToTaskDetailsDialog(
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
                            onDeleteTask = onDeleteTask,
                            onNavigateToTaskStatisticsDialog = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToTaskStatisticsDialog(
                                            args = args
                                        )
                                )
                            },
                            onDuplicateTask = onDuplicateTask,
                            onToggleArchiveTask = onToggleArchiveTask,
                            onNavigateToTaskHashtags = onNavigateToTaskHashtags
                        )
                        if (it.children.isNotEmpty() && isTaskExpanded) {
                            ChildrenTasks(
                                taskWithHashtagsAndChildrenModel = it.children,
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
                }

                is InboxScreenUiModel.Section -> {
                    stickyHeader {
                        SectionCardHeader(
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = tween(),
                                    placementSpec = spring(),
                                    fadeOutSpec = tween()
                                ),
                            sectionModel = inboxScreenModel.section,
                            childrenCount = inboxScreenModel.tasks.size,
                            onToggleChildren = {
                                onToggleSection(inboxScreenModel.section.sectionId)
                            },
                            onNavigateToAddTask = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToAddTaskDialog(
                                            args = args
                                        )
                                )
                            },
                            isSectionExpanded = getIsSectionExpanded(inboxScreenModel.section.sectionId),
                            onDeleteSection = onDeleteSection,
                            onNavigateToUpdateSectionDialog = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToUpdateSectionTitleDialog(
                                            args = args
                                        )
                                )
                            }
                        )

                    }
                    if (getIsSectionExpanded(inboxScreenModel.section.sectionId)) {
                        items(
                            items = inboxScreenModel.tasks,
                            key = { item: TaskWithHashtagsAndChildrenModel ->
                                item.taskWithUiHashtagsModel.task.taskId
                            }
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
                    }
                }

            }
        }


        item {
            Spacer(modifier = Modifier.height(96.dp))
        }

    }

}