package at.robthered.plan_me.features.common.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


val PriorityMedium: ImageVector
    get() {
        if (_PriorityMedium != null) {
            return _PriorityMedium!!
        }
        _PriorityMedium = ImageVector.Builder(
            name = "PriorityMedium",
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
                moveTo(3f, 20f)
                lineTo(10.586f, 12.414f)
                curveTo(11.367f, 11.633f, 12.633f, 11.633f, 13.414f, 12.414f)
                lineTo(21f, 20f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(3f, 13f)
                lineTo(10.586f, 5.414f)
                curveTo(11.367f, 4.633f, 12.633f, 4.633f, 13.414f, 5.414f)
                lineTo(21f, 13f)
            }
        }.build()

        return _PriorityMedium!!
    }

@Suppress("ObjectPropertyName")
private var _PriorityMedium: ImageVector? = null