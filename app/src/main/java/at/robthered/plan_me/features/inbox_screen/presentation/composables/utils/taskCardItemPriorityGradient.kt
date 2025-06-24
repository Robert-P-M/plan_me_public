package at.robthered.plan_me.features.inbox_screen.presentation.composables.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.presentation.ext.model.backgroundColor

@Composable
fun taskCardItemPriorityGradient(
    priorityEnum: PriorityEnum?,
    defaultBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
): ShaderBrush {
    val priorityColor by animateColorAsState(
        targetValue = priorityEnum.backgroundColor(MaterialTheme.colorScheme.surfaceVariant),
        animationSpec = tween(durationMillis = 400),
        label = "priority color animation"
    )

    val transition = remember { Animatable(1f) }

    LaunchedEffect(Unit, priorityColor) {
        transition.animateTo(2f)
    }

    return remember(priorityColor, transition.value) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return RadialGradientShader(
                    center = Offset(
                        x = -(size.width * 2.3f),
                        y = -(size.width * 1.8f)
                    ),
                    radius = size.width * 4f,
                    colorStops = listOf(0f, 0.8f, 1f),
                    colors = listOf(
                        defaultBackgroundColor.copy(alpha = 0.5f),
                        defaultBackgroundColor.copy(alpha = 0.5f),
                        priorityColor.copy(alpha = 0.7f)
                    ),
                    tileMode = TileMode.Clamp
                )
            }
        }
    }
}