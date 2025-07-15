package at.robthered.plan_me.features.common.presentation.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SearchResultLeft: ImageVector
    get() {
        if (_SearchResultLeft != null) {
            return _SearchResultLeft!!
        }
        _SearchResultLeft = ImageVector.Builder(
            name = "SearchResultLeft",
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
                moveTo(10f, 12f)
                horizontalLineTo(2f)
                moveTo(2f, 12f)
                lineTo(5.111f, 9f)
                moveTo(2f, 12f)
                lineTo(5.111f, 15f)
            }
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9.646f, 9f)
                curveTo(10.47f, 8.079f, 11.667f, 7.5f, 13f, 7.5f)
                curveTo(15.485f, 7.5f, 17.5f, 9.515f, 17.5f, 12f)
                curveTo(17.5f, 13.247f, 16.993f, 14.382f, 16.174f, 15.2f)
                moveTo(9.646f, 14.915f)
                curveTo(10.472f, 15.802f, 11.551f, 16.483f, 13f, 16.5f)
                curveTo(14.239f, 16.514f, 15.361f, 16.014f, 16.174f, 15.2f)
                moveTo(16.174f, 15.2f)
                lineTo(21.5f, 20.5f)
            }
        }.build()

        return _SearchResultLeft!!
    }

@Suppress("ObjectPropertyName")
private var _SearchResultLeft: ImageVector? = null