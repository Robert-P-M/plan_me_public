package at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.ui.presentation.modifier.borderBottom
import at.robthered.plan_me.features.ui.presentation.modifier.borderTop


/**
 * Composable function that renders a dropdown menu item based on the provided [MenuItem] type.
 *
 * This is a sealed class wrapper that delegates the rendering of different menu item types
 * (Action, Default, Header, Close, ViewTypeItem) to their respective [AppDropdownMenuItem] overloads.
 *
 * @param modifier Optional [Modifier] for this dropdown menu item.
 * @param menuItem The [MenuItem] object that determines the type and content of the menu item.
 */
@Composable
fun AppDropdownMenuItem(
    modifier: Modifier = Modifier,
    menuItem: MenuItem,
) {
    when (menuItem) {
        is MenuItem.Header -> AppDropdownMenuItem(
            menuItem = menuItem,
            modifier = modifier,
        )

        is MenuItem.Action -> AppDropdownMenuItem(
            menuItem = menuItem,
            modifier = modifier
        )

        is MenuItem.Default -> AppDropdownMenuItem(
            menuItem = menuItem,
            modifier = modifier
        )

        is MenuItem.Footer -> AppDropdownMenuItem(
            menuItem = menuItem,
            modifier = modifier
        )

        is MenuItem.Close -> AppDropdownMenuItem(
            menuItem = menuItem,
            modifier = modifier
        )

        is MenuItem.ViewTypeItem -> AppDropdownMenuItem(
            menuItem,
            modifier,
        )
    }
}

/**
 * Composable function to display a dropdown menu item with optional leading and trailing icons.
 *
 * This function utilizes the Material Design DropdownMenuItem composable to create a standard
 * menu item within a dropdown menu. It supports displaying text, an optional leading icon,
 * and an optional trailing checkmark icon to indicate if the item is active.
 *
 * @param menuItem The [MenuItem.Default] data class containing the text, optional leading icon,
 * and the click handler for the menu item.
 * @param modifier Optional [Modifier] for customizing the appearance and layout of the menu item.
 * @param isActive A boolean indicating whether the menu item should display a trailing checkmark icon
 * to signify an active state. Defaults to false.
 */
@Composable
private fun AppDropdownMenuItem(
    menuItem: MenuItem.Default,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier,
        text = {
            Text(
                text = menuItem.text.asString(),
                textAlign = TextAlign.End
            )
        },
        leadingIcon = {
            menuItem.leadingIcon?.let { imageVector ->
                Icon(
                    imageVector = imageVector,
                    contentDescription = menuItem.iconDescription?.asString()
                )
            }
        },
        trailingIcon = {
            if (isActive) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = menuItem.iconDescription?.asString(),
                    tint = Color.Green.copy(alpha = 0.8f)
                )
            }
        },
        onClick = { menuItem.onClick() }
    )
}

/**
 * A composable function that represents a single item within a dropdown menu in the application.
 *
 * This function renders a [DropdownMenuItem] with customized styling and content based on the
 * provided [menuItem] and its active state.
 *
 * @param menuItem The data class containing information for this menu item, including the text,
 *   leading icon, icon description, and the action to perform when the item is clicked.
 *   It's of type [MenuItem.ViewTypeItem].
 * @param modifier Optional [Modifier] for customizing the appearance and layout of the
 *   [DropdownMenuItem]. Defaults to [Modifier].
 * @param isActive A boolean flag indicating whether this menu item is currently active or
 *   selected. If `true`, the item will have a distinct background and a checkmark trailing icon.
 *   Defaults to `false`.
 */
@Composable
private fun AppDropdownMenuItem(
    menuItem: MenuItem.ViewTypeItem,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier
            .then(
                if (menuItem.isActive) {
                    Modifier
                        .clip(
                            RoundedCornerShape(4.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                } else Modifier
            ),
        text = {
            Text(
                text = menuItem.text.asString(),
                textAlign = TextAlign.End
            )
        },
        leadingIcon = {
            Icon(
                imageVector = menuItem.leadingIcon,
                contentDescription = menuItem.iconDescription?.asString()
            )
        },
        trailingIcon = {
            if (menuItem.isActive) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = stringResource(id = R.string.view_type_picker_dialog_checked_icon),
                    tint = Color.Green.copy(alpha = 0.8f)
                )
            }
        },
        onClick = { menuItem.onClick() }
    )
}

/**
 * Represents a single menu item within a dropdown menu, specifically designed for a header type.
 * This composable displays the title of the menu item and includes a visual separator below it.
 * It uses [DropdownMenuItem] as the underlying composable.
 *
 * @param menuItem The [MenuItem.Footer] data class containing the information for this menu item,
 *   specifically the title to be displayed.
 * @param modifier A [Modifier] to be applied to the [DropdownMenuItem]. Allows for customization
 *   of the layout and appearance.
 */
@Composable
private fun AppDropdownMenuItem(
    menuItem: MenuItem.Footer,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier
            .borderTop(MaterialTheme.colorScheme.onSurfaceVariant),
        text = {
            Text(
                text = menuItem.creation.asString()
            )
        },
        onClick = {

        }
    )
}

@Composable
private fun AppDropdownMenuItem(
    menuItem: MenuItem.Header,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier
            .borderBottom(MaterialTheme.colorScheme.onSurfaceVariant),
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = menuItem.title.asString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
        },
        onClick = {

        }
    )
}

/**
 * Composable function to display a dropdown menu item based on the provided [MenuItem.Action] data.
 *
 * This function renders a standard [DropdownMenuItem] from the Material Design library,
 * customizing its appearance and behavior based on the properties of the [MenuItem.Action] object.
 * It allows setting text, leading icon, background color, text color, icon tint, and click action.
 *
 * @param menuItem The data class containing all the information needed to display the dropdown menu item.
 *                 This includes text, optional leading icon, optional icon description, optional background color,
 *                 optional text color, optional icon tint, and the required click action.
 * @param modifier Optional [Modifier] for customizing the layout and appearance of the menu item.
 */
@Composable
private fun AppDropdownMenuItem(
    menuItem: MenuItem.Action,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier.then(
            if (menuItem.backgroundColor != null) {
                Modifier.background(menuItem.backgroundColor)
            } else Modifier
        ),
        text = {
            Text(
                text = menuItem.text.asString(),
                color = menuItem.textColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        },
        leadingIcon = {
            menuItem.leadingIcon?.let { imageVector ->
                Icon(
                    imageVector = imageVector,
                    contentDescription = menuItem.iconDescription?.asString(),
                    tint = menuItem.iconTint ?: MaterialTheme.colorScheme.surfaceTint
                )
            }
        },
        trailingIcon = {
            menuItem.trailingIcon?.let { imageVector ->
                Icon(
                    imageVector = imageVector,
                    contentDescription = menuItem.iconDescription?.asString(),
                    tint = menuItem.iconTint ?: MaterialTheme.colorScheme.surfaceTint
                )
            }
        },
        onClick = menuItem.onClick
    )
}

/**
 * Composable function to display a single item within an application's dropdown menu.
 *
 * This specific implementation creates a menu item designed for a "Close" action,
 * featuring a "Close" text label and a corresponding close icon.
 *
 * @param menuItem The [MenuItem.Close] object containing the action to be performed
 * when this menu item is clicked. It specifically requires a [MenuItem.Close] because
 * this Composable is tailored to that specific type of menu item.
 * @param modifier Optional [Modifier] to apply to this dropdown menu item.
 * This allows for customization of the item's appearance or behavior, such as padding,
 * layout weight, or click handling outside of the default onClick.
 */
@Composable
private fun AppDropdownMenuItem(
    menuItem: MenuItem.Close,
    modifier: Modifier = Modifier,
) {
    DropdownMenuItem(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = modifier,
        text = {
            Text(
                text = UiText.StringResource(R.string.dropdown_menu_item_text_close).asString(),
                textAlign = TextAlign.End
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = UiText.StringResource(R.string.dropdown_menu_item_icon_description_close)
                    .asString(),
            )
        },
        onClick = menuItem.onClick
    )
}