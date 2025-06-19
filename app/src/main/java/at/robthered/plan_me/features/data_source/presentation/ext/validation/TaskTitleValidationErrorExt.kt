package at.robthered.plan_me.features.data_source.presentation.ext.validation

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.validation.TaskTitleValidationError

fun TaskTitleValidationError.toUiText(): UiText {
    return when (this) {
        TaskTitleValidationError.TOO_SHORT -> UiText.StringResource(
            id = R.string.task_title_validation_error_too_short,
            args = listOf(TaskTitleValidationError.MIN_LENGTH)
        )

        TaskTitleValidationError.OVERFLOW -> UiText.StringResource(
            id = R.string.task_title_validation_error_overflow,
            args = listOf(TaskTitleValidationError.MAX_LENGTH)
        )

        TaskTitleValidationError.EMPTY -> UiText.StringResource(
            id = R.string.task_title_validation_error_empty,
        )
    }
}