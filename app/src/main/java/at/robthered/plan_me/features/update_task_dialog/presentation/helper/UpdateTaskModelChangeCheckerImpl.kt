package at.robthered.plan_me.features.update_task_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel

// TODO: TESTS

class UpdateTaskModelChangeCheckerImpl : UpdateTaskModelChangeChecker {
    override operator fun invoke(
        initialModel: UpdateTaskModel,
        currentModel: UpdateTaskModel,
    ): Boolean {
        return initialModel.title != currentModel.title ||
                initialModel.description != currentModel.description ||
                initialModel.priorityEnum != currentModel.priorityEnum
    }
}