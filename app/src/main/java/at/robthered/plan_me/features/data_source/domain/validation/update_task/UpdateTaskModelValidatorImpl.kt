package at.robthered.plan_me.features.data_source.domain.validation.update_task

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModelError
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidator

class UpdateTaskModelValidatorImpl(
    private val taskTitleValidator: TaskTitleValidator,
    private val taskDescriptionValidator: TaskDescriptionValidator,
) : UpdateTaskModelValidator {
    override operator fun invoke(updateTaskModel: UpdateTaskModel): UpdateTaskModelError {
        return UpdateTaskModelError(
            title = taskTitleValidator(value = updateTaskModel.title).getErrorOrNull(),
            description = taskDescriptionValidator(value = updateTaskModel.description).getErrorOrNull()
        )
    }
}