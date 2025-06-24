package at.robthered.plan_me.features.data_source.presentation.ext.validation

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.validation.SectionTitleValidationError

fun SectionTitleValidationError.toUiText(): UiText {
    return when (this) {
        SectionTitleValidationError.TOO_SHORT ->
            UiText.StringResource(
                id = R.string.section_title_validation_too_short,
                args = listOf(SectionTitleValidationError.MIN_LENGTH)
            )

        SectionTitleValidationError.OVERFLOW ->
            UiText.StringResource(
                id = R.string.section_title_validation_overflow,
                args = listOf(SectionTitleValidationError.MAX_LENGTH)
            )

        SectionTitleValidationError.EMPTY ->
            UiText.StringResource(
                id = R.string.section_title_validation_empty,
            )
    }
}