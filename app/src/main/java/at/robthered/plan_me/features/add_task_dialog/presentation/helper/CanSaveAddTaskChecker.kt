package at.robthered.plan_me.features.add_task_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError

interface CanSaveAddTaskChecker {
    operator fun invoke(addTaskModelError: AddTaskModelError): Boolean
}