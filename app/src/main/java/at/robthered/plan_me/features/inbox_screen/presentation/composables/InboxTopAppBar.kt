package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.view_type.ViewTypeEnum
import at.robthered.plan_me.features.data_source.domain.model.view_type.icon
import at.robthered.plan_me.features.data_source.domain.model.view_type.text
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenu
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenuItem
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.MenuItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InboxTopAppBar(
    modifier: Modifier = Modifier,
    onChangeViewType: (viewTypeEnum: ViewTypeEnum) -> Unit,
    currentViewType: ViewTypeEnum,
    showCompleted: Boolean?,
    showArchived: Boolean?,
    onSetShowCompleted: (Boolean?) -> Unit,
    onSetShowArchived: (Boolean?) -> Unit,
    sortDirection: SortDirection,
    onSetSortDirection: (SortDirection) -> Unit,
) {

    var isMenuExpanded by remember {
        mutableStateOf(false)
    }
    val density = LocalDensity.current

    var parentItemSize by remember { mutableStateOf(IntSize.Zero) }
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "Inbox"
            )
        },
        actions = {
            Box() {
                AppDropdownMenu(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        parentItemSize = coordinates.size
                    },
                    expanded = isMenuExpanded,
                    onDismissRequest = {
                        isMenuExpanded = false
                    }
                ) {
                    AppDropdownMenuItem(
                        menuItem = MenuItem.Header(
                            title = UiText.StringResource(id = R.string.inbox_top_app_bar_header_text)
                        )
                    )
                    var showViewTypeMenu by remember {
                        mutableStateOf(false)
                    }
                    val offset = with(density) {
                        DpOffset(
                            x = parentItemSize.width.toDp(),
                            y = 0.dp
                        )
                    }
                    var showFilterMenu by remember {
                        mutableStateOf(false)
                    }
                    var showSortOrderDirectionMenu by remember {
                        mutableStateOf(false)
                    }
                    AppDropdownMenuItem(
                        menuItem = MenuItem.ViewTypeItem(
                            text = UiText.StringResource(
                                id = R.string.inbox_top_app_bar_view_type_picker
                            ),
                            leadingIcon = currentViewType.icon(),
                            iconDescription = currentViewType.text(),
                            viewTypeEnum = currentViewType,
                            onClick = { showViewTypeMenu = true }
                        )
                    )
                    AppDropdownMenuItem(
                        menuItem = MenuItem.Default(
                            text = UiText.StringResource(
                                id = R.string.inbox_top_app_bar_filter
                            ),
                            leadingIcon = Icons.Outlined.FilterAlt,
                            iconDescription = UiText.StringResource(
                                id = R.string.inbox_top_app_bar_filter
                            ),
                            onClick = { showFilterMenu = true }
                        )
                    )
                    AppDropdownMenuItem(
                        menuItem = MenuItem.Default(
                            text = UiText.StringResource(
                                id = R.string.inbox_top_app_bar_sort_order
                            ),
                            leadingIcon = Icons.AutoMirrored.Outlined.Sort,
                            iconDescription = UiText.StringResource(
                                id = R.string.inbox_top_app_bar_sort_order
                            ),
                            onClick = { showSortOrderDirectionMenu = true }
                        )
                    )
                    AppDropdownMenu(
                        expanded = showSortOrderDirectionMenu,
                        onDismissRequest = { showSortOrderDirectionMenu = false }
                    ) {
                        AppDropdownMenuItem(
                            menuItem = MenuItem.Action(
                                text = UiText.StringResource(
                                    id = R.string.inbox_top_app_bar_sort_order_desc
                                ),
                                leadingIcon = Icons.Outlined.ArrowDropUp,
                                iconDescription = UiText.StringResource(
                                    id = R.string.inbox_top_app_bar_sort_order_desc
                                ),
                                onClick = {
                                    onSetSortDirection(SortDirection.DESC)
                                    showSortOrderDirectionMenu = false
                                },
                                trailingIcon =
                                    if (sortDirection == SortDirection.DESC) {
                                        Icons.Outlined.Check
                                    } else null

                            )
                        )

                        AppDropdownMenuItem(
                            menuItem = MenuItem.Action(
                                text = UiText.StringResource(
                                    id = R.string.inbox_top_app_bar_sort_order_asc
                                ),
                                leadingIcon = Icons.Outlined.ArrowDropDown,
                                iconDescription = UiText.StringResource(
                                    id = R.string.inbox_top_app_bar_sort_order_asc
                                ),
                                onClick = {
                                    onSetSortDirection(SortDirection.ASC)
                                    showSortOrderDirectionMenu = false
                                },
                                trailingIcon =
                                    if (sortDirection == SortDirection.ASC) {
                                        Icons.Outlined.Check
                                    } else null
                            )
                        )
                    }
                    AppDropdownMenu(
                        expanded = showFilterMenu,
                        offset = offset,
                        onDismissRequest = {
                            showFilterMenu = false
                            isMenuExpanded = false
                        }
                    ) {

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = "Filter Completed",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedToggleButton(
                                    checked = showCompleted == null,
                                    onCheckedChange = { onSetShowCompleted(null) },
                                ) {
                                    Text(text = "All")
                                }
                                OutlinedToggleButton(
                                    checked = showCompleted == false,
                                    onCheckedChange = { onSetShowCompleted(false) },
                                ) {
                                    Text(text = "Hide completed")
                                }
                                OutlinedToggleButton(
                                    checked = showCompleted == true,
                                    onCheckedChange = { onSetShowCompleted(true) },
                                ) {
                                    Text(text = "Show completed")
                                }

                            }
                        }

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = "Filter Archived",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedToggleButton(
                                    checked = showArchived == null,
                                    onCheckedChange = { onSetShowArchived(null) },
                                ) {
                                    Text(text = "All")
                                }
                                OutlinedToggleButton(
                                    checked = showArchived == false,
                                    onCheckedChange = { onSetShowArchived(false) },
                                ) {
                                    Text(text = "Hide archived")
                                }
                                OutlinedToggleButton(
                                    checked = showArchived == true,
                                    onCheckedChange = { onSetShowArchived(true) },
                                ) {
                                    Text(text = "Show archived")
                                }

                            }
                        }
                    }
                    AppDropdownMenu(
                        expanded = showViewTypeMenu,
                        offset = offset,
                        onDismissRequest = {
                            showViewTypeMenu = false
                            isMenuExpanded = false
                        }
                    ) {
                        AppDropdownMenuItem(
                            menuItem = MenuItem.ViewTypeItem(
                                text = UiText.StringResource(id = R.string.inbox_top_app_bar_view_type_board_text),
                                leadingIcon = Icons.Outlined.ViewColumn,
                                iconDescription = UiText.StringResource(id = R.string.inbox_top_app_bar_view_type_board_text),
                                onClick = {
                                    onChangeViewType(ViewTypeEnum.BoardView)
                                    isMenuExpanded = false
                                },
                                viewTypeEnum = ViewTypeEnum.BoardView,
                                isActive = currentViewType == ViewTypeEnum.BoardView
                            ),

                            )
                        AppDropdownMenuItem(
                            menuItem = MenuItem.ViewTypeItem(
                                text = UiText.StringResource(id = R.string.inbox_top_app_bar_view_type_list_text),
                                leadingIcon = Icons.AutoMirrored.Outlined.ViewList,
                                iconDescription = UiText.StringResource(id = R.string.inbox_top_app_bar_view_type_list_description),
                                onClick = {
                                    onChangeViewType(ViewTypeEnum.ListView)
                                    isMenuExpanded = false
                                },
                                viewTypeEnum = ViewTypeEnum.ListView,
                                isActive = currentViewType == ViewTypeEnum.ListView
                            ),

                            )
                    }
                    AppDropdownMenuItem(
                        menuItem = MenuItem.Close(
                            onClick = {
                                isMenuExpanded = false
                            }
                        )
                    )
                }
                IconButton(
                    onClick = {
                        isMenuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Settings"
                    )
                }
            }
        },
    )

}