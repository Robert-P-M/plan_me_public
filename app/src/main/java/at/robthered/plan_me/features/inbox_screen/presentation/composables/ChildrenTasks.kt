package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.PriorityPickerUpdateArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel

@Composable
fun ChildrenTasks(
    modifier: Modifier = Modifier,
    taskWithHashtagsAndChildrenModel: List<TaskWithHashtagsAndChildrenModel>,
    getIsTaskExpanded: (taskId: Long) -> Boolean,
    onNavigateToUpdateTaskDialog: (args: UpdateTaskDialogArgs) -> Unit,
    onToggleChildren: (taskId: Long) -> Unit,
    onNavigateToTaskStatisticsDialog: (args: TaskStatisticsDialogArgs) -> Unit,
    onNavigateToAddTask: (args: AddTaskDialogArgs) -> Unit,
    onNavigateToPriorityPicker: (args: PriorityPickerUpdateArgs) -> Unit,
    onToggleCompleteTask: (taskId: Long, title: String, isCompleted: Boolean) -> Unit,
    depth: Int,
    onDeleteTask: (taskId: Long) -> Unit,
    onNavigateToTaskDetailsDialog: (args: TaskDetailsDialogArgs) -> Unit,
    onDuplicateTask: (Long) -> Unit,
    onToggleArchiveTask: (Long, String, Boolean) -> Unit,
    onNavigateToTaskHashtags: (args: TaskHashtagsDialogArgs) -> Unit,
    onNavigateToMoveTaskDialog: (MoveTaskDialogArgs) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(start = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        taskWithHashtagsAndChildrenModel.mapIndexed { index, taskWithChildrenModel ->
            val isChildTaskExpanded by remember {
                derivedStateOf {
                    getIsTaskExpanded(taskWithChildrenModel.taskWithUiHashtagsModel.task.taskId)
                }
            }
            TaskCardHeader(
                taskWithHashtagsAndChildrenModel = taskWithChildrenModel,
                onNavigateToAddTask = onNavigateToAddTask,
                onNavigateToPriorityPicker = onNavigateToPriorityPicker,
                onNavigateToUpdateTaskDialog = onNavigateToUpdateTaskDialog,
                onNavigateToTaskStatisticsDialog = onNavigateToTaskStatisticsDialog,
                onToggleChildren = { onToggleChildren(taskWithChildrenModel.taskWithUiHashtagsModel.task.taskId) },
                isTaskExpanded = isChildTaskExpanded,
                onToggleCompleteTask = onToggleCompleteTask,
                onToggleArchiveTask = onToggleArchiveTask,
                onDuplicateTask = onDuplicateTask,
                depth = depth + 1,
                onDeleteTask = onDeleteTask,
                onNavigateToToTaskDetailsDialog = onNavigateToTaskDetailsDialog,
                onNavigateToTaskHashtags = onNavigateToTaskHashtags,
                onNavigateToMoveTaskDialog = onNavigateToMoveTaskDialog
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 2.dp)
            )
            if (taskWithChildrenModel.children.isNotEmpty() && getIsTaskExpanded(
                    taskWithChildrenModel.taskWithUiHashtagsModel.task.taskId
                )
            ) {
                ChildrenTasks(
                    taskWithHashtagsAndChildrenModel = taskWithChildrenModel.children,
                    getIsTaskExpanded = getIsTaskExpanded,
                    onNavigateToUpdateTaskDialog = onNavigateToUpdateTaskDialog,
                    onToggleChildren = onToggleChildren,
                    onNavigateToTaskStatisticsDialog = onNavigateToTaskStatisticsDialog,
                    onNavigateToAddTask = onNavigateToAddTask,
                    onNavigateToPriorityPicker = onNavigateToPriorityPicker,
                    onToggleCompleteTask = onToggleCompleteTask,
                    depth = depth + 1,
                    onDeleteTask = onDeleteTask,
                    onNavigateToTaskDetailsDialog = onNavigateToTaskDetailsDialog,
                    onDuplicateTask = onDuplicateTask,
                    onToggleArchiveTask = onToggleArchiveTask,
                    onNavigateToTaskHashtags = onNavigateToTaskHashtags,
                    onNavigateToMoveTaskDialog = onNavigateToMoveTaskDialog
                )
            }
        }


    }
}