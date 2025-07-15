package at.robthered.plan_me.features.common.presentation.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SectionIcon: ImageVector
    get() {
        if (_SectionIcon != null) {
            return _SectionIcon!!
        }
        _SectionIcon = ImageVector.Builder(
            name = "SectionIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(22f, 14f)
                verticalLineTo(10f)
                curveTo(22f, 7.791f, 20.209f, 6f, 18f, 6f)
                horizontalLineTo(6f)
                curveTo(3.791f, 6f, 2f, 7.791f, 2f, 10f)
                verticalLineTo(14f)
                curveTo(2f, 16.209f, 3.791f, 18f, 6f, 18f)
                horizontalLineTo(18f)
                curveTo(20.209f, 18f, 22f, 16.209f, 22f, 14f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2f, 21f)
                horizontalLineTo(22f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2f, 3f)
                horizontalLineTo(22f)
            }
        }.build()

        return _SectionIcon!!
    }

@Suppress("ObjectPropertyName")
private var _SectionIcon: ImageVector? = null