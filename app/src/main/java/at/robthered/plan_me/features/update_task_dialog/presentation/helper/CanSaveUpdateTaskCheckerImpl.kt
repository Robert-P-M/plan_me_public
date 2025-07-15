package at.robthered.plan_me.features.update_task_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModelError

// TODO: TESTS

class CanSaveUpdateTaskCheckerImpl : CanSaveUpdateTaskChecker {
    override operator fun invoke(updateTaskModelError: UpdateTaskModelError): Boolean {
        return updateTaskModelError.title == null && updateTaskModelError.description == null
    }
}