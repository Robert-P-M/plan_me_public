package at.robthered.plan_me.features.data_source.presentation.ext.validation

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.validation.TaskDescriptionValidationError

fun TaskDescriptionValidationError.toUiText(): UiText {
    return when (this) {
        TaskDescriptionValidationError.TOO_SHORT ->
            UiText
                .StringResource(
                    id = R.string.task_description_validation_error_too_short,
                    args = listOf(TaskDescriptionValidationError.MIN_LENGTH)
                )

        TaskDescriptionValidationError.OVERFLOW ->
            UiText
                .StringResource(
                    id = R.string.task_description_validation_error_overflow,
                    args = listOf(TaskDescriptionValidationError.MAX_LENGTH)
                )
    }
}