package at.robthered.plan_me.features.common.presentation

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle


@Composable
private fun List<Any>.resolve(): List<Any> {
    return this.map { if (it is UiText) it.asString() else it }
}

private fun List<Any>.resolve(context: Context): List<Any> {
    return this.map { if (it is UiText) it.asString(context) else it }
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> {
            context.resources.getString(
                /* id = */ id, /*
                ...formatArgs = */ *args.resolve(context).toTypedArray()
            )
        }

        is UiText.PluralResource -> {
            context.resources.getQuantityString(
                /* id = */ id,
                /* quantity = */ count,
                /* ...formatArgs = */ *args.resolve(context).toTypedArray()
            )
        }

        is UiText.Annotated -> annotatedString.text
    }
}


@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResource -> {

            stringResource(
                id = id,
                formatArgs = args.resolve().toTypedArray()
            )
        }

        is UiText.PluralResource -> {
            pluralStringResource(
                id = id,
                count = count,
                formatArgs = args.resolve().toTypedArray()
            )
        }

        is UiText.Annotated -> annotatedString.text
    }
}

@Composable
fun UiText.asAnnotatedString(
    color: Color = MaterialTheme.colorScheme.primary,
    textDecoration: TextDecoration? = null,
): AnnotatedString {

    return when (this) {
        is UiText.DynamicString -> AnnotatedString(value)
        is UiText.StringResource -> {
            val resolvedArgs = args.resolve()
            val formattedText = stringResource(
                id = id,
                formatArgs = resolvedArgs.toTypedArray()
            )
            buildAnnotatedString {
                appendFormattedText(
                    formattedText = formattedText,
                    resolvedArgs = resolvedArgs,
                    color = color,
                    textDecoration = textDecoration
                )
            }
        }

        is UiText.PluralResource -> {
            val resolvedArgs = args.resolve()
            val formattedText = pluralStringResource(
                id = id,
                count = count,
                formatArgs = resolvedArgs.toTypedArray()
            )
            buildAnnotatedString {
                appendFormattedText(
                    formattedText = formattedText,
                    resolvedArgs = resolvedArgs,
                    color = color,
                    textDecoration = textDecoration
                )
            }
        }

        is UiText.Annotated -> annotatedString


    }
}

private fun AnnotatedString.Builder.appendFormattedText(
    formattedText: String,
    resolvedArgs: List<Any>,
    color: Color,
    textDecoration: TextDecoration?,
) {
    if (resolvedArgs.isEmpty()) {
        append(formattedText)
        return
    }
    var lastIndex = 0
    resolvedArgs.forEach { arg ->
        val placeholder = arg.toString()
        if (placeholder.isEmpty()) return@forEach
        val startIndex = formattedText.indexOf(placeholder, lastIndex)
        if (startIndex != -1) {
            append(formattedText.substring(lastIndex, startIndex))
            withStyle(
                style = SpanStyle(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    textDecoration = textDecoration,
                ),

                ) {
                append(placeholder)
            }
            lastIndex = startIndex + placeholder.length
        }
    }
    append(formattedText.substring(lastIndex))
}