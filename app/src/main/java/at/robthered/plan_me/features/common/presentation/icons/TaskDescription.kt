package at.robthered.plan_me.features.common.presentation.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TaskDescription: ImageVector
    get() {
        if (_TaskDescription != null) {
            return _TaskDescription!!
        }
        _TaskDescription = ImageVector.Builder(
            name = "TaskDescription",
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
                moveTo(4f, 4.5f)
                horizontalLineTo(20f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(4f, 9.5f)
                horizontalLineTo(20f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(4f, 14.5f)
                horizontalLineTo(12f)
                horizontalLineTo(16f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(4f, 19.5f)
                horizontalLineTo(12f)
            }
        }.build()

        return _TaskDescription!!
    }

@Suppress("ObjectPropertyName")
private var _TaskDescription: ImageVector? = null