package at.robthered.plan_me.features.task_details_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

sealed interface TaskDetailsDialogNavigationEvent {
    data object OnNavigateBack : TaskDetailsDialogNavigationEvent
    data class OnNavigateToTaskDetailsDialogDialog(val taskId: Long) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToSectionDetailsDialog(val sectionId: Long) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToAddTaskDialog(val args: AddTaskDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data object OnNavigateToPriorityPickerDialog : TaskDetailsDialogNavigationEvent

    data class OnNavigateToUpdateTaskDialog(val args: UpdateTaskDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToTaskStatisticsDialog(val args: TaskStatisticsDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToUpdateHashtagNameDialog(val args: UpdateHashtagNameDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToTaskHashtagsDialog(val args: TaskHashtagsDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToHashtagTasksDialog(val args: HashtagTasksDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToMoveTaskDialog(val args: MoveTaskDialogArgs) :
        TaskDetailsDialogNavigationEvent

    data class OnNavigateToTaskScheduleEventDialog(val args: TaskSchedulePickerDialogArgs) :
        TaskDetailsDialogNavigationEvent
}