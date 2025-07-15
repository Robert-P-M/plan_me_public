package at.robthered.plan_me.features.data_source.domain.validation.add_task

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidator
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidator

class AddTaskModelValidatorImpl(
    private val taskTitleValidator: TaskTitleValidator,
    private val taskDescriptionValidator: TaskDescriptionValidator,
) : AddTaskModelValidator {
    override operator fun invoke(addTaskModel: AddTaskModel): AddTaskModelError {
        return AddTaskModelError(
            title = taskTitleValidator(value = addTaskModel.title).getErrorOrNull(),
            description = taskDescriptionValidator(value = addTaskModel.description).getErrorOrNull()
        )
    }
}