package at.robthered.plan_me.features.data_source.domain.validation.add_task

import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError

interface AddTaskModelValidator {
    operator fun invoke(addTaskModel: AddTaskModel): AddTaskModelError
}