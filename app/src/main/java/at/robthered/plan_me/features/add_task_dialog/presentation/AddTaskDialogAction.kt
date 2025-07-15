package at.robthered.plan_me.features.add_task_dialog.presentation

import at.robthered.plan_me.features.common.presentation.navigation.TaskSchedulePickerDialogArgs
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

sealed interface AddTaskDialogAction {
    data class OnChangeTitle(val title: String) : AddTaskDialogAction
    data class OnChangeDescription(val description: String?) : AddTaskDialogAction
    data class OnChangePriority(val priority: PriorityEnum?) : AddTaskDialogAction
    data class OnChangeHashtags(val hashtags: List<UiHashtagModel>) : AddTaskDialogAction
    data object OnNavigateToPriorityPickerDialog : AddTaskDialogAction
    data object OnNavigateToHashtagPickerDialog : AddTaskDialogAction
    data object OnResetState : AddTaskDialogAction
    data object OnSaveTask : AddTaskDialogAction
    data object OnNavigateBack : AddTaskDialogAction
    data class OnNavigateToTaskSchedulePickerDialog(val args: TaskSchedulePickerDialogArgs) :
        AddTaskDialogAction

    data object OnRemoveTaskScheduleEvent : AddTaskDialogAction
}