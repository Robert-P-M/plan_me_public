package at.robthered.plan_me.features.common.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PriorityNormal: ImageVector
    get() {
        if (_PriorityNormal != null) {
            return _PriorityNormal!!
        }
        _PriorityNormal = ImageVector.Builder(
            name = "PriorityNormal",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(2f, 17f)
                lineTo(10.125f, 9.303f)
                curveTo(10.896f, 8.572f, 12.104f, 8.572f, 12.875f, 9.303f)
                lineTo(21f, 17f)
            }
        }.build()

        return _PriorityNormal!!
    }

@Suppress("ObjectPropertyName")
private var _PriorityNormal: ImageVector? = null