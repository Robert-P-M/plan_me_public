package at.robthered.plan_me.features.common.presentation.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AddTaskIcon: ImageVector
    get() {
        if (_AddTaskIcon != null) {
            return _AddTaskIcon!!
        }
        _AddTaskIcon = ImageVector.Builder(
            name = "AddTaskIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 7.455f)
                verticalLineTo(6.727f)
                curveTo(3f, 5.623f, 3.895f, 4.727f, 5f, 4.727f)
                horizontalLineTo(7.5f)
                verticalLineTo(2.9f)
                curveTo(7.5f, 2.403f, 7.903f, 2f, 8.4f, 2f)
                verticalLineTo(2f)
                curveTo(8.897f, 2f, 9.3f, 2.403f, 9.3f, 2.9f)
                verticalLineTo(4.727f)
                horizontalLineTo(14.7f)
                verticalLineTo(2.9f)
                curveTo(14.7f, 2.403f, 15.103f, 2f, 15.6f, 2f)
                verticalLineTo(2f)
                curveTo(16.097f, 2f, 16.5f, 2.403f, 16.5f, 2.9f)
                verticalLineTo(4.727f)
                horizontalLineTo(19f)
                curveTo(20.105f, 4.727f, 21f, 5.623f, 21f, 6.727f)
                verticalLineTo(7.455f)
                moveTo(3f, 7.455f)
                verticalLineTo(20f)
                curveTo(3f, 21.105f, 3.895f, 22f, 5f, 22f)
                horizontalLineTo(12f)
                horizontalLineTo(19f)
                curveTo(20.105f, 22f, 21f, 21.105f, 21f, 20f)
                verticalLineTo(14.727f)
                verticalLineTo(7.455f)
                moveTo(3f, 7.455f)
                horizontalLineTo(21f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 14.5f)
                lineTo(16f, 14.5f)
                moveTo(12f, 10.5f)
                verticalLineTo(14.5f)
                lineTo(12f, 18.5f)
            }
        }.build()

        return _AddTaskIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AddTaskIcon: ImageVector? = null