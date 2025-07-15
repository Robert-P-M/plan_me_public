package at.robthered.plan_me.features.data_source.domain.validation

import at.robthered.plan_me.features.common.domain.ValidationError

enum class HashtagNameValidationError : ValidationError {
    TOO_SHORT, OVERFLOW, EMPTY;

    companion object {
        const val MIN_LENGTH = 2
        const val MAX_LENGTH = 15
    }
}