package at.robthered.plan_me.features.data_source.presentation.ext.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.icons.PriorityHigh
import at.robthered.plan_me.features.common.presentation.icons.PriorityLow
import at.robthered.plan_me.features.common.presentation.icons.PriorityMedium
import at.robthered.plan_me.features.common.presentation.icons.PriorityNormal
import at.robthered.plan_me.features.common.presentation.icons.PriorityPicker
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.ui.presentation.theme.PlanMeTheme

@Composable
fun PriorityEnum?.backgroundColor(default: Color = MaterialTheme.colorScheme.surfaceVariant): Color {
    return when (this) {
        PriorityEnum.LOW -> PlanMeTheme.extendedColors.priorityLow.colorContainer
        PriorityEnum.MEDIUM -> PlanMeTheme.extendedColors.priorityMedium.colorContainer
        PriorityEnum.NORMAL -> PlanMeTheme.extendedColors.priorityNormal.colorContainer
        PriorityEnum.HIGH -> PlanMeTheme.extendedColors.priorityHigh.colorContainer
        null -> default
    }
}

@Composable
fun PriorityEnum?.borderBrush(): Brush {
    val baseBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

    val currentPriorityColor = this.backgroundColor(MaterialTheme.colorScheme.surfaceVariant)
    val priorityBorderColor = currentPriorityColor.copy(alpha = 1f)

    return remember(baseBorderColor, priorityBorderColor) {
        Brush.linearGradient(
            colors = listOf(baseBorderColor, priorityBorderColor),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    }
}

@Composable
fun PriorityEnum?.iconTint(default: Color = MaterialTheme.colorScheme.onSurfaceVariant): Color {
    return when (this) {
        PriorityEnum.LOW -> PlanMeTheme.extendedColors.priorityLow.onColorContainer
        PriorityEnum.MEDIUM -> PlanMeTheme.extendedColors.priorityMedium.onColorContainer
        PriorityEnum.NORMAL -> PlanMeTheme.extendedColors.priorityNormal.onColorContainer
        PriorityEnum.HIGH -> PlanMeTheme.extendedColors.priorityHigh.onColorContainer
        null -> default
    }
}

fun PriorityEnum?.imageVector(): ImageVector {
    return when (this) {
        PriorityEnum.LOW -> PriorityLow
        PriorityEnum.NORMAL -> PriorityNormal
        PriorityEnum.MEDIUM -> PriorityMedium
        PriorityEnum.HIGH -> PriorityHigh
        null -> PriorityPicker
    }
}

fun PriorityEnum?.toUiText(short: Boolean = true): UiText {
    return when (this) {
        PriorityEnum.LOW -> UiText.StringResource(
            id = if (short) R.string.priority_enum_low_text_short else R.string.priority_enum_low_text_long,
        )

        PriorityEnum.MEDIUM -> UiText.StringResource(
            id = if (short) R.string.priority_enum_medium_text_short else R.string.priority_enum_medium_text_long,
        )

        PriorityEnum.NORMAL -> UiText.StringResource(
            id = if (short) R.string.priority_enum_normal_text_short else R.string.priority_enum_normal_text_long,
        )

        PriorityEnum.HIGH -> UiText.StringResource(
            id = if (short) R.string.priority_enum_high_text_short else R.string.priority_enum_high_text_long,
        )

        null -> UiText.StringResource(
            id = if (short) R.string.priority_enum_null_text_short else R.string.priority_enum_null_text_long,
        )
    }
}