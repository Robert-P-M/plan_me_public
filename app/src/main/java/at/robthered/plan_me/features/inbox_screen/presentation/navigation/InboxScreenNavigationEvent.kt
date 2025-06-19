package at.robthered.plan_me.features.inbox_screen.presentation.navigation

import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateSectionTitleDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs

sealed interface InboxScreenNavigationEvent {

    data class OnNavigateToAddTaskDialog(
        val args: AddTaskDialogArgs,
    ) : InboxScreenNavigationEvent

    data class OnNavigateToUpdateTaskDialog(
        val args: UpdateTaskDialogArgs,
    ) : InboxScreenNavigationEvent

    data object OnNavigateToAddSectionDialog
        : InboxScreenNavigationEvent

    data class OnNavigateToUpdateSectionTitleDialog(
        val args: UpdateSectionTitleDialogArgs,
    ) : InboxScreenNavigationEvent

    data object OnNavigateToPriorityPickerDialog
        : InboxScreenNavigationEvent

    data class OnNavigateToTaskDetailsDialog(
        val args: TaskDetailsDialogArgs,
    ) : InboxScreenNavigationEvent

    data class OnNavigateToTaskStatisticsDialog(
        val args: TaskStatisticsDialogArgs,
    ) : InboxScreenNavigationEvent

    data class OnNavigateToTaskHashtagsDialog(
        val args: TaskHashtagsDialogArgs,
    ) : InboxScreenNavigationEvent

    data class OnNavigateToMoveTaskDialog(val args: MoveTaskDialogArgs) : InboxScreenNavigationEvent
}