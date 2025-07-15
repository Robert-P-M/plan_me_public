package at.robthered.plan_me.features.task_statistics_dialog.presentation

import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs

sealed interface TaskStatisticsDialogUiAction {
    data object OnNavigateBack : TaskStatisticsDialogUiAction
    data class OnDeleteTaskTitleHistory(val taskTitleHistoryId: Long) : TaskStatisticsDialogUiAction
    data class OnDeleteTaskDescriptionHistory(val taskDescriptionHistoryId: Long) :
        TaskStatisticsDialogUiAction

    data class OnDeleteTaskPriorityHistory(val taskPriorityHistoryId: Long) :
        TaskStatisticsDialogUiAction

    data class OnDeleteTaskCompletedHistory(val taskCompletedHistoryId: Long) :
        TaskStatisticsDialogUiAction

    data class OnDeleteTaskArchivedHistory(val taskArchivedHistoryId: Long) :
        TaskStatisticsDialogUiAction

    data class OnDeleteTaskScheduleEvent(val taskScheduleEventId: Long) :
        TaskStatisticsDialogUiAction

    data class OnDeleteSubTask(val subTaskId: Long) : TaskStatisticsDialogUiAction
    data class OnNavigateToTaskDetailsDialog(val args: TaskDetailsDialogArgs) :
        TaskStatisticsDialogUiAction

    data class OnNavigateToHashtagTasksDialog(val args: HashtagTasksDialogArgs) :
        TaskStatisticsDialogUiAction
}