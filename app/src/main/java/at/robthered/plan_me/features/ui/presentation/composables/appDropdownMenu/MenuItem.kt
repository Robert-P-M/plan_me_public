package at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.model.view_type.ViewTypeEnum

/**
 * Represents a distinct item that can be displayed in a menu.
 *
 * This sealed class provides different types of menu items to cater to various display needs.
 */
sealed class MenuItem {
    data class Header(
        val title: UiText,
    ) : MenuItem()

    data class Footer(
        val creation: UiText,
    ) : MenuItem()

    data class Default(
        val text: UiText,
        val leadingIcon: ImageVector? = null,
        val iconTint: Color? = null,
        val iconDescription: UiText? = null,
        val onClick: () -> Unit = {},
    ) : MenuItem()

    data class Action(
        val text: UiText,
        val leadingIcon: ImageVector? = null,
        val iconDescription: UiText? = null,
        val onClick: () -> Unit = {},
        val textColor: Color? = null,
        val iconTint: Color? = null,
        val backgroundColor: Color? = null,
        val trailingIcon: ImageVector? = null,
    ) : MenuItem()

    data class Close(
        val onClick: () -> Unit,
    ) : MenuItem()


    data class ViewTypeItem(
        val text: UiText,
        val leadingIcon: ImageVector,
        val iconDescription: UiText? = null,
        val onClick: () -> Unit,
        val viewTypeEnum: ViewTypeEnum,
        val isActive: Boolean = false,
    ) : MenuItem()
}