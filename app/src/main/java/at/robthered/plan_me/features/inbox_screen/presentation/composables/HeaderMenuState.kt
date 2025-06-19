package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.MenuItem
import at.robthered.plan_me.features.ui.presentation.utils.date_time.toFormattedForMenuHeader
import kotlinx.datetime.Instant

class HeaderMenuState(
    createdAt: Instant,
    additionalItems: (MutableList<MenuItem>.(state: HeaderMenuState) -> Unit)? = null,
) {
    var isOpen by mutableStateOf(false)
        private set

    fun onShow() {
        isOpen = true
    }

    fun onHide() {
        isOpen = false
    }

    fun onToggle() {
        isOpen = isOpen.not()
    }

    val menuItems: List<MenuItem> =
        buildList {
            add(
                MenuItem.Header(
                    title = UiText.StringResource(
                        R.string.task_card_header_dropdown_menu_item_text_menu,
                    ),
                )
            )
            additionalItems?.invoke(this, this@HeaderMenuState)
            add(
                MenuItem.Close(
                    onClick = { onHide() },
                )
            )
            add(
                MenuItem.Footer(
                    creation = UiText.StringResource(
                        R.string.task_card_header_dropdown_menu_item_text_created_at,
                        listOf(createdAt.toFormattedForMenuHeader())
                    ),
                )
            )
        }
}


@Composable
fun rememberHeaderMenuState(
    createdAt: Instant,
    additionalItems: (MutableList<MenuItem>.(state: HeaderMenuState) -> Unit)? = null,
    vararg keys: Any?,
): HeaderMenuState {

    return remember(*keys) {
        HeaderMenuState(
            createdAt = createdAt,
            additionalItems = additionalItems,
        )
    }
}