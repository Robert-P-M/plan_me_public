package at.robthered.plan_me.features.data_source.domain.validation.update_task

import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModelError

interface UpdateTaskModelValidator {
    operator fun invoke(updateTaskModel: UpdateTaskModel): UpdateTaskModelError
}