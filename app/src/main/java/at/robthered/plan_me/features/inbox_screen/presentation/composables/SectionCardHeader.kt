package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateSectionTitleDialogArgs
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.ui.presentation.composables.AppDeleteDialog
import at.robthered.plan_me.features.ui.presentation.composables.HeaderRow
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenu
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.AppDropdownMenuItem
import at.robthered.plan_me.features.ui.presentation.composables.appDropdownMenu.MenuItem


@Composable
fun SectionCardHeader(
    modifier: Modifier = Modifier,
    sectionModel: SectionModel,
    childrenCount: Int,
    onToggleChildren: () -> Unit,
    onNavigateToAddTask: (AddTaskDialogArgs) -> Unit,
    isSectionExpanded: Boolean,
    onDeleteSection: (sectionId: Long) -> Unit,
    onNavigateToUpdateSectionDialog: (args: UpdateSectionTitleDialogArgs) -> Unit,
    showExpandIcon: Boolean = true,
) {

    var showConfirmDeleteSectionDialog by remember {
        mutableStateOf(false)
    }

    if (showConfirmDeleteSectionDialog) {
        val deleteTaskText = UiText
            .StringResource(
                id = R.string.delete_section_dialog_text,
                args = listOf(stringResource(R.string.delete_section_dialog_text_info))
            )
        AppDeleteDialog(
            modifier = Modifier,
            onDismissRequest = { showConfirmDeleteSectionDialog = false },
            onAccept = {
                onDeleteSection(sectionModel.sectionId)
            },
            title = stringResource(R.string.delete_section_dialog_title),
            text = deleteTaskText.asAnnotatedString(color = MaterialTheme.colorScheme.error)
        )
    }

    val errorTextColor = MaterialTheme.colorScheme.onErrorContainer
    val errorIconTint = MaterialTheme.colorScheme.onErrorContainer
    val errorBackgroundColor = MaterialTheme.colorScheme.errorContainer
    val sectionCardHeaderMenu = rememberHeaderMenuState(
        createdAt = sectionModel.createdAt,
        keys = arrayOf(sectionModel),
        additionalItems = {
            add(
                MenuItem.Default(
                    text = UiText.StringResource(R.string.section_card_header_dropdown_menu_item_add_sub_task_text),
                    leadingIcon = Icons.Outlined.AddCircle,
                    iconDescription = UiText.StringResource(R.string.section_card_header_dropdown_menu_item_add_sub_task_icon_description),
                    onClick = {
                        it.onHide()
                        onNavigateToAddTask(
                            AddTaskDialogArgs(
                                sectionId = sectionModel.sectionId
                            )
                        )
                    },
                )
            )
            add(
                MenuItem.Action(
                    text = UiText.StringResource(
                        id = R.string.section_card_header_dropdown_menu_item_update_title_text,
                    ),
                    leadingIcon = Icons.Outlined.Edit,
                    iconDescription = UiText.StringResource(
                        id = R.string.section_card_header_dropdown_menu_item_update_title_text,
                    ),
                    onClick = {
                        it.onHide()
                        onNavigateToUpdateSectionDialog(
                            UpdateSectionTitleDialogArgs(sectionId = sectionModel.sectionId)
                        )
                    }
                )
            )
            add(
                MenuItem.Action(
                    text = UiText.StringResource(
                        id = R.string.section_card_header_dropdown_menu_item_delete_section_text,
                    ),
                    leadingIcon = Icons.Outlined.Delete,
                    iconDescription = UiText.StringResource(
                        id = R.string.section_card_header_dropdown_menu_item_delete_section_text,
                    ),
                    onClick = {
                        showConfirmDeleteSectionDialog = true
                        it.onHide()
                    },
                    textColor = errorTextColor,
                    iconTint = errorIconTint,
                    backgroundColor = errorBackgroundColor,
                )
            )
        }
    )

    val haptics = LocalHapticFeedback.current
    HeaderRow(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .combinedClickable(
                onDoubleClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    sectionCardHeaderMenu.onShow()
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onLongClickLabel = stringResource(R.string.section_card_header_dropdown_menu_long_press_open_menu),
                onClick = {}
            ),
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = sectionModel.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Box() {

            AppDropdownMenu(
                expanded = sectionCardHeaderMenu.isOpen,
                onDismissRequest = { sectionCardHeaderMenu.onHide() }
            ) {
                sectionCardHeaderMenu.menuItems.map { menuItem ->
                    AppDropdownMenuItem(
                        menuItem = menuItem
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                if (childrenCount > 0) {
                    Text(
                        text = childrenCount.toString(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    if (showExpandIcon) {
                        ChildrenToggleButton(
                            isExpanded = isSectionExpanded,
                            onToggle = onToggleChildren
                        )

                    }
                }
                Icon(
                    modifier = Modifier
                        .clickable(
                            enabled = true,
                            onClick = { sectionCardHeaderMenu.onShow() }
                        ),
                    imageVector = Icons.Outlined.MoreHoriz,
                    contentDescription = "Section Menu",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}