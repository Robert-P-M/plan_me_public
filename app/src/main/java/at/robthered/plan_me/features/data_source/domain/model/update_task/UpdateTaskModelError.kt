package at.robthered.plan_me.features.data_source.domain.model.update_task

import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidationError
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidationError

data class UpdateTaskModelError(
    val title: TaskTitleValidationError? = null,
    val description: TaskDescriptionValidationError? = null,
)