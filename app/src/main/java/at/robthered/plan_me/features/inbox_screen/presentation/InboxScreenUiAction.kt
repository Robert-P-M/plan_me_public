package at.robthered.plan_me.features.inbox_screen.presentation

import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.PriorityPickerUpdateArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskHashtagsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.TaskStatisticsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateSectionTitleDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.view_type.ViewTypeEnum

sealed interface InboxScreenUiAction {
    data class OnToggleSection(val sectionId: Long) : InboxScreenUiAction
    data class OnToggleTask(val taskId: Long) : InboxScreenUiAction
    data class OnChangeViewType(val viewType: ViewTypeEnum) : InboxScreenUiAction
    data class OnToggleCompleteTask(val taskId: Long, val title: String, val isCompleted: Boolean) :
        InboxScreenUiAction

    data class OnToggleArchiveTask(val taskId: Long, val title: String, val isArchived: Boolean) :
        InboxScreenUiAction

    data class OnDeleteTask(val taskId: Long) : InboxScreenUiAction
    data class OnDeleteSection(val sectionId: Long) : InboxScreenUiAction
    data class OnNavigateToPriorityPickerDialog(val args: PriorityPickerUpdateArgs) :
        InboxScreenUiAction

    data class OnNavigateToTaskDetailsDialog(val args: TaskDetailsDialogArgs) : InboxScreenUiAction
    data object OnNavigateToAddSectionDialog : InboxScreenUiAction
    data class OnDuplicateTask(val taskId: Long) : InboxScreenUiAction
    data class OnNavigateToTaskStatisticsDialog(val args: TaskStatisticsDialogArgs) :
        InboxScreenUiAction

    data class OnNavigateToAddTaskDialog(val args: AddTaskDialogArgs) : InboxScreenUiAction
    data class OnNavigateToUpdateTaskDialog(val args: UpdateTaskDialogArgs) : InboxScreenUiAction
    data class OnNavigateToUpdateSectionTitleDialog(val args: UpdateSectionTitleDialogArgs) :
        InboxScreenUiAction

    data class OnSetShowCompleted(val showCompleted: Boolean?) : InboxScreenUiAction
    data class OnSetShowArchived(val showArchived: Boolean?) : InboxScreenUiAction
    data class OnSetSortDirection(val sortDirection: SortDirection) : InboxScreenUiAction
    data class OnNavigateToTaskHashtagsDialog(val args: TaskHashtagsDialogArgs) :
        InboxScreenUiAction

    data class OnNavigateToMoveTaskDialog(val args: MoveTaskDialogArgs) : InboxScreenUiAction
}