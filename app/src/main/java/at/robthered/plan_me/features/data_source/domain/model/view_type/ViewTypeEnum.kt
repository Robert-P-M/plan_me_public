package at.robthered.plan_me.features.data_source.domain.model.view_type

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.ui.graphics.vector.ImageVector
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText

enum class ViewTypeEnum {
    BoardView,
    ListView,
}

fun ViewTypeEnum.text(): UiText {
    return when (this) {
        ViewTypeEnum.BoardView -> UiText
            .StringResource(
                id = R.string.inbox_top_app_bar_view_type_list_text,
            )

        ViewTypeEnum.ListView -> UiText
            .StringResource(
                id = R.string.inbox_top_app_bar_view_type_board_text
            )
    }
}

fun ViewTypeEnum.icon(): ImageVector {
    return when (this) {
        ViewTypeEnum.BoardView -> Icons.Outlined.ViewColumn
        ViewTypeEnum.ListView -> Icons.AutoMirrored.Outlined.ViewList
    }
}