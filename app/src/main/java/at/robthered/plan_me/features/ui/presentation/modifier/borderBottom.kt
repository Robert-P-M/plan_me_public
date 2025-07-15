package at.robthered.plan_me.features.ui.presentation.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.borderBottom(strokeColor: Color) = drawBehind {
    drawLine(
        start = Offset(
            x = 0f,
            y = size.height
        ),
        end = Offset(
            x = size.width,
            y = size.height
        ),
        strokeWidth = 2f,
        color = strokeColor
    )
}

@Composable
fun Modifier.borderTop(strokeColor: Color) = drawBehind {
    drawLine(
        start = Offset(
            x = 0f,
            y = 0f,
        ),
        end = Offset(
            x = size.width,
            y = 0f,
        ),
        strokeWidth = 2f,
        color = strokeColor
    )
}