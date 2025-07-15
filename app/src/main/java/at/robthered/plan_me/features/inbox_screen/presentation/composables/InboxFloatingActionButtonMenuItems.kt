package at.robthered.plan_me.features.inbox_screen.presentation.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.features.common.domain.model.AppFloatingActionButtonMenuItem
import at.robthered.plan_me.features.common.presentation.icons.AddSectionIcon
import at.robthered.plan_me.features.common.presentation.icons.AddTaskIcon
import at.robthered.plan_me.features.common.presentation.navigation.AddTaskDialogArgs

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingActionButtonMenuScope.InboxFloatingActionButtonMenuItems(
    onHide: () -> Unit,
    onNavigateToAddSectionDialog: () -> Unit,
    onNavigateToAddTaskDialog: (args: AddTaskDialogArgs) -> Unit,
) {
    val floatingActionButtonMenuItems = listOf(
        AppFloatingActionButtonMenuItem(
            text = "Add Task",
            onClick = { onNavigateToAddTaskDialog(AddTaskDialogArgs()) },
            imageVector = AddTaskIcon,
            contentDescription = "Add Task Icon"
        ),
        AppFloatingActionButtonMenuItem(
            text = "Add Section",
            onClick = onNavigateToAddSectionDialog,
            imageVector = AddSectionIcon,
            contentDescription = "Add Section Icon"
        ),
    )

    floatingActionButtonMenuItems.forEachIndexed { index, item ->
        FloatingActionButtonMenuItem(
            modifier = Modifier.Companion
                .semantics {
                    isTraversalGroup = true
                    if (index == floatingActionButtonMenuItems.size - 1) {
                        customActions =
                            listOf(
                                CustomAccessibilityAction(
                                    label = "Close Menu",
                                    action = {
                                        onHide()
                                        true
                                    }
                                )
                            )
                    }
                },
            onClick = {
                onHide()
                item.onClick()

            },
            icon = {
                Icon(
                    imageVector = item.imageVector,
                    contentDescription = item.text,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = item.text,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
        )
    }
}