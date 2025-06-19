package at.robthered.plan_me.features.data_source.presentation.ext.validation

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidationError

fun HashtagNameValidationError.toUiText(): UiText {
    return when (this) {
        HashtagNameValidationError.TOO_SHORT -> UiText
            .StringResource(
                id = R.string.hashtag_name_validation_error_too_short,
                args = listOf(HashtagNameValidationError.MIN_LENGTH)
            )

        HashtagNameValidationError.OVERFLOW -> UiText
            .StringResource(
                id = R.string.hashtag_name_validation_error_overflow,
                args = listOf(HashtagNameValidationError.MAX_LENGTH)
            )

        HashtagNameValidationError.EMPTY -> UiText
            .StringResource(
                id = R.string.hashtag_name_validation_error_empty,
            )
    }
}