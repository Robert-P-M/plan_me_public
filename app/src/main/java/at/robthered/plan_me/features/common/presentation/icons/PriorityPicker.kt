package at.robthered.plan_me.features.common.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


val PriorityPicker: ImageVector
    get() {
        if (_PriorityPicker != null) {
            return _PriorityPicker!!
        }
        _PriorityPicker = ImageVector.Builder(
            name = "PriorityPicker",
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
                moveTo(12f, 12f)
                horizontalLineTo(16.5f)
                horizontalLineTo(21f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 9f)
                lineTo(15.836f, 5.591f)
                curveTo(16.215f, 5.254f, 16.785f, 5.254f, 17.164f, 5.591f)
                lineTo(21f, 9f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 15f)
                lineTo(15.836f, 18.41f)
                curveTo(16.215f, 18.746f, 16.785f, 18.746f, 17.164f, 18.41f)
                lineTo(21f, 15f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8.079f, 21.024f)
                lineTo(10.806f, 18.809f)
                curveTo(11.023f, 18.633f, 11.128f, 18.374f, 11.117f, 18.119f)
                moveTo(8.295f, 15.133f)
                lineTo(10.853f, 17.542f)
                curveTo(11.02f, 17.7f, 11.108f, 17.908f, 11.117f, 18.119f)
                moveTo(11.117f, 18.119f)
                curveTo(10.116f, 18.329f, 9.051f, 18.313f, 7.995f, 18.03f)
                curveTo(4.401f, 17.067f, 2.268f, 13.373f, 3.231f, 9.779f)
                curveTo(3.713f, 7.981f, 4.877f, 6.55f, 6.37f, 5.688f)
                curveTo(7.374f, 5.108f, 9.245f, 4.5f, 11.117f, 5f)
            }
        }.build()

        return _PriorityPicker!!
    }

@Suppress("ObjectPropertyName")
private var _PriorityPicker: ImageVector? = null