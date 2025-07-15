package at.robthered.plan_me.features.common.presentation

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString

sealed interface UiText {
    data class DynamicString(
        val value: String,
    ) : UiText

    data class StringResource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    data class PluralResource(
        @PluralsRes val id: Int,
        val count: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    data class Annotated(
        val annotatedString: AnnotatedString,
    ) : UiText

}