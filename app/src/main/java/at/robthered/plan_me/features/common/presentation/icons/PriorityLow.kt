package at.robthered.plan_me.features.common.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PriorityLow: ImageVector
    get() {
        if (_PriorityLow != null) {
            return _PriorityLow!!
        }
        _PriorityLow = ImageVector.Builder(
            name = "PriorityLow",
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
                moveTo(3f, 8f)
                lineTo(10.586f, 15.586f)
                curveTo(11.367f, 16.367f, 12.633f, 16.367f, 13.414f, 15.586f)
                lineTo(21f, 8f)
            }
        }.build()

        return _PriorityLow!!
    }

@Suppress("ObjectPropertyName")
private var _PriorityLow: ImageVector? = null