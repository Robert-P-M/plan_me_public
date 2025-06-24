package at.robthered.plan_me.features.task_details_dialog.presentation

import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

sealed interface TaskDetailsDialogUiAction {
    data class OnNavigateToTaskDetailsDialog(val taskId: Long) : TaskDetailsDialogUiAction
    data class OnNavigateToAddTaskDialog(val args: AddTaskDialogArgs) : TaskDetailsDialogUiAction
    data class OnNavigateToSectionDetailsDialog(val sectionId: Long) : TaskDetailsDialogUiAction
    data object OnNavigateBack : TaskDetailsDialogUiAction
    data class OnToggleCompleteTask(val taskId: Long, val title: String, val isCompleted: Boolean) :
        TaskDetailsDialogUiAction

    data class OnToggleArchiveTask(val taskId: Long, val title: String, val isArchived: Boolean) :
        TaskDetailsDialogUiAction

    data class OnNavigateToPriorityPickerDialog(
        val priorityEnum: PriorityEnum?,
        val taskId: Long,
    ) : TaskDetailsDialogUiAction

    data class OnDeleteTask(val taskId: Long, val navigateBack: Boolean = true) :
        TaskDetailsDialogUiAction

    data class OnNavigateToUpdateTaskDialog(val args: UpdateTaskDialogArgs) :
        TaskDetailsDialogUiAction

    data class OnNavigateToTaskStatisticsDialog(val args: TaskStatisticsDialogArgs) :
        TaskDetailsDialogUiAction

    data class OnNavigateToUpdateHashtagNameDialog(val args: UpdateHashtagNameDialogArgs) :
        TaskDetailsDialogUiAction

    data class OnDeleteTaskHashtagReference(val taskId: Long, val hashtagId: Long) :
        TaskDetailsDialogUiAction

    data class OnNavigateToTaskHashtagsDialog(val args: TaskHashtagsDialogArgs) :
        TaskDetailsDialogUiAction

    data class OnNavigateToHashtagTasksDialog(val args: HashtagTasksDialogArgs) :
        TaskDetailsDialogUiAction

    data class OnNavigateToMoveTaskDialog(val args: MoveTaskDialogArgs) : TaskDetailsDialogUiAction
    data class OnNavigateToTaskScheduleEventPickerDialog(val args: TaskSchedulePickerDialogArgs) :
        TaskDetailsDialogUiAction
}