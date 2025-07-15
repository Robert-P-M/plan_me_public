package at.robthered.plan_me.features.move_task_dialog.presentation

import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel

sealed interface MoveTaskDialogUiAction {
    data object OnNavigateBack : MoveTaskDialogUiAction
    data class OnPickMoveTaskItem(val moveTaskModel: MoveTaskModel) : MoveTaskDialogUiAction
}