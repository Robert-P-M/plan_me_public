package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.ValidationError

enum class TaskDescriptionValidationError : ValidationError {
    TOO_SHORT, OVERFLOW;

    companion object {
        const val MIN_LENGTH = 3
        const val MAX_LENGTH = 1500
    }
}