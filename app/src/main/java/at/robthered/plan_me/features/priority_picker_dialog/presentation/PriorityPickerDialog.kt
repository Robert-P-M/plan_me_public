package at.robthered.plan_me.features.priority_picker_dialog.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.icons.PriorityHigh
import at.robthered.plan_me.features.common.presentation.icons.PriorityLow
import at.robthered.plan_me.features.common.presentation.icons.PriorityMedium
import at.robthered.plan_me.features.common.presentation.icons.PriorityNormal
import at.robthered.plan_me.features.common.presentation.icons.PriorityPicker
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.presentation.ext.model.iconTint
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.PriorityPickerDialogNavigationActions
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.PriorityPickerDialogNavigationEvent
import at.robthered.plan_me.features.ui.presentation.composables.AppDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun PriorityPickerDialogRoot(
    priorityPickerDialogViewModel: PriorityPickerDialogViewModel = koinViewModel<PriorityPickerDialogViewModel>(),
    priorityPickerDialogNavigationActions: PriorityPickerDialogNavigationActions,
) {

    val currentPriority by priorityPickerDialogViewModel.currentPriority.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = priorityPickerDialogViewModel.appNavigationEvent) {
        when (it) {
            PriorityPickerDialogNavigationEvent.OnNavigateBack -> {
                priorityPickerDialogNavigationActions.onNavigateBack()
            }
        }
    }

    PriorityPickerDialog(
        currentPriority = currentPriority,
        onAction = priorityPickerDialogViewModel::onAction,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityPickerDialog(
    onAction: (action: PriorityPickerDialogUiAction) -> Unit,
    currentPriority: PriorityEnum?,
) {
    val priorityItems = remember {
        buildList {
            add(
                PriorityItem(
                    text = UiText.StringResource(R.string.priority_enum_low_text_long),
                    leadingIcon = PriorityLow,
                    priority = PriorityEnum.LOW
                )
            )
            add(
                PriorityItem(
                    text = UiText.StringResource(R.string.priority_enum_normal_text_long),
                    leadingIcon = PriorityNormal,
                    priority = PriorityEnum.NORMAL
                )
            )
            add(
                PriorityItem(
                    text = UiText.StringResource(R.string.priority_enum_medium_text_long),
                    leadingIcon = PriorityMedium,
                    priority = PriorityEnum.MEDIUM
                )
            )
            add(
                PriorityItem(
                    text = UiText.StringResource(R.string.priority_enum_high_text_long),
                    leadingIcon = PriorityHigh,
                    priority = PriorityEnum.HIGH
                )
            )
        }
    }

    AppDialog(
        onDismissRequest = { onAction(PriorityPickerDialogUiAction.OnNavigateBack) },
    ) {
        val priorityListState = rememberLazyListState()

        LazyColumn(
            state = priorityListState,
        ) {
            stickyHeader {
                PriorityPickerDialogItem(
                    modifier = Modifier,
                    onDismissRequest = { onAction(PriorityPickerDialogUiAction.OnNavigateBack) },
                )
            }
            item {

                PriorityPickerDialogItem(
                    modifier = Modifier,
                    onPickPriority = {
                        onAction(
                            PriorityPickerDialogUiAction
                                .OnPickPriority(priorityEnum = it)
                        )
                    }
                )
            }
            items(priorityItems) { item: PriorityItem ->
                PriorityPickerDialogItem(
                    modifier = Modifier,
                    onPickPriority = {
                        onAction(
                            PriorityPickerDialogUiAction
                                .OnPickPriority(priorityEnum = it)
                        )
                    },
                    item = item,
                    currentPriority = currentPriority,
                )
            }
        }
    }

}

data class PriorityItem(
    val priority: PriorityEnum,
    val text: UiText,
    val leadingIcon: ImageVector,
)

/**
 * Dialog Item for header
 */
@Composable
fun PriorityPickerDialogItem(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(true) {
                onDismissRequest()
            }
            .fillMaxWidth()
            .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = PriorityPicker,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.priority_enum_no_selection_text_long),
                style = MaterialTheme.typography.titleSmall,
            )

            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Selected item checkmark",
            )

        }
    }
}

/**
 * Dialog Item for priorities
 */
@Composable
fun PriorityPickerDialogItem(
    modifier: Modifier,
    onPickPriority: (priority: PriorityEnum?) -> Unit,
    item: PriorityItem,
    currentPriority: PriorityEnum?,
) {
    Box(
        modifier = modifier
            .clickable(true) {
                onPickPriority(item.priority)

            }
            .fillMaxWidth()
            .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.leadingIcon,
                contentDescription = null,
                tint = item.priority.iconTint()
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                text = item.text.asString(),
                style = MaterialTheme.typography.titleSmall,
                color = item.priority.iconTint()
            )
            if (item.priority == currentPriority) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Selected item checkmark",
                    tint = Color.Green.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Dialog Item for reset priority
 */
@Composable
fun PriorityPickerDialogItem(
    modifier: Modifier,
    onPickPriority: (priority: PriorityEnum?) -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(true) {
                onPickPriority(null)
            }
            .fillMaxWidth()
            .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error.copy(0.8f)
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                text = UiText.StringResource(R.string.priority_enum_remove_text_long).asString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error.copy(0.8f)
            )
        }
    }
}