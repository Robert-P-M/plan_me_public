package at.robthered.plan_me.features.common.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PriorityHigh: ImageVector
    get() {
        if (_PriorityHigh != null) {
            return _PriorityHigh!!
        }
        _PriorityHigh = ImageVector.Builder(
            name = "PriorityHigh",
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
                moveTo(3f, 21f)
                lineTo(10.586f, 13.414f)
                curveTo(11.367f, 12.633f, 12.633f, 12.633f, 13.414f, 13.414f)
                lineTo(21f, 21f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3f, 12f)
                lineTo(10.586f, 4.414f)
                curveTo(11.367f, 3.633f, 12.633f, 3.633f, 13.414f, 4.414f)
                lineTo(21f, 12f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3f, 16.5f)
                lineTo(10.586f, 8.914f)
                curveTo(11.367f, 8.133f, 12.633f, 8.133f, 13.414f, 8.914f)
                lineTo(21f, 16.5f)
            }
        }.build()

        return _PriorityHigh!!
    }

@Suppress("ObjectPropertyName")
private var _PriorityHigh: ImageVector? = null