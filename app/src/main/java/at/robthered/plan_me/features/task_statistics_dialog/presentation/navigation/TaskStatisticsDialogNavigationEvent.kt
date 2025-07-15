package at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs

sealed interface TaskStatisticsDialogNavigationEvent {
    data object OnNavigateBack : TaskStatisticsDialogNavigationEvent
    data class OnNavigateToTaskDetailsDialog(val args: TaskDetailsDialogArgs) :
        TaskStatisticsDialogNavigationEvent

    data class OnNavigateToHashtagTasksDialog(val args: HashtagTasksDialogArgs) :
        TaskStatisticsDialogNavigationEvent
}