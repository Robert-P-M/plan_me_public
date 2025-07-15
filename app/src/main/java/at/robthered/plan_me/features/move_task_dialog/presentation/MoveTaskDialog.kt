package at.robthered.plan_me.features.move_task_dialog.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.SwipeRightAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import at.robthered.plan_me.features.data_source.presentation.ext.model.imageVector
import at.robthered.plan_me.features.data_source.presentation.ext.model.isCurrent
import at.robthered.plan_me.features.data_source.presentation.ext.model.isOwn
import at.robthered.plan_me.features.data_source.presentation.ext.model.title
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.MoveTaskDialogNavigationActions
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.MoveTaskDialogNavigationEvent
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.theme.PlanMeTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun MoveTaskDialogRoot(
    modifier: Modifier = Modifier,
    moveTaskDialogNavigationActions: MoveTaskDialogNavigationActions,
    moveTaskDialogViewModel: MoveTaskDialogViewModel = koinViewModel<MoveTaskDialogViewModel>(),
) {
    val moveTaskItems by moveTaskDialogViewModel.moveTaskItems.collectAsStateWithLifecycle()
    val moveTaskDialogArgs by moveTaskDialogViewModel.moveTaskDialogArgs.collectAsStateWithLifecycle()
    val isLoading by moveTaskDialogViewModel.isLoading.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = moveTaskDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            MoveTaskDialogNavigationEvent.OnNavigateBack ->
                moveTaskDialogNavigationActions
                    .onNavigateBack()
        }
    }


    MoveTaskDialog(
        modifier = modifier,
        moveTaskModels = moveTaskItems,
        moveTaskDialogArgs = moveTaskDialogArgs,
        isLoading = isLoading,
        onAction = moveTaskDialogViewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoveTaskDialog(
    modifier: Modifier = Modifier,
    moveTaskModels: List<MoveTaskModel>,
    onAction: (MoveTaskDialogUiAction) -> Unit,
    moveTaskDialogArgs: MoveTaskDialogArgs,
    isLoading: Boolean,
) {

    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = true,
        onNavigateBack = {
            onAction(
                MoveTaskDialogUiAction
                    .OnNavigateBack
            )
        }
    )
    val abortDialogText = UiText.StringResource(
        id = R.string.move_task_dialog_confirm_back_navigation_text,
    )

    if (appSheetState.showConfirmationDialog) {
        AppAbortDialog(
            onDismissRequest = {
                appSheetState.cancelDiscardChanges()
            },
            onAccept = {
                appSheetState.confirmDiscardAndNavigateBack()
            },
            title = stringResource(R.string.move_task_dialog_confirm_back_navigation_title),
            text = abortDialogText.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
            confirmText = stringResource(R.string.app_abort_dialog_close),
            infoColor = MaterialTheme.colorScheme.primary,
            onInfoColor = MaterialTheme.colorScheme.onPrimary,
        )
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            appSheetState.requestHide()
        },
        sheetState = appSheetState.sheetState,
        dragHandle = null,
    ) {
        val listState = rememberLazyListState()


        LazyColumn(
            modifier = Modifier,
            state = listState,
        ) {
            if (isLoading) {
                item {
                    AnimatedVisibility(
                        visible = isLoading,
                        enter = fadeIn(
                            animationSpec = tween(
                                delayMillis = 100
                            )
                        ) + expandVertically(),
                        exit = fadeOut() + shrinkVertically(
                            animationSpec = tween(
                                delayMillis = 100
                            )
                        )
                    ) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            stickyHeader {
                AppModalTitle(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    title = stringResource(R.string.move_task_dialog_modal_title),
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = {
                                appSheetState.requestHide()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
            itemsIndexed(
                items = moveTaskModels
            ) { index, moveTaskItem ->
                Row(
                    modifier = Modifier
                        .animateItem(
                            fadeInSpec = tween(),
                            placementSpec = spring(),
                            fadeOutSpec = tween()
                        )
                        .alpha(if (moveTaskItem.isOwn(moveTaskDialogArgs = moveTaskDialogArgs)) 0.2f else 1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .then(
                            if (moveTaskItem is MoveTaskModel.Task) {
                                Modifier.padding(start = (moveTaskItem.depth * 15).dp)
                            } else {
                                Modifier
                            }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        imageVector = moveTaskItem.imageVector(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = moveTaskItem.title()
                    )
                    if (moveTaskItem.isCurrent(moveTaskDialogArgs = moveTaskDialogArgs)) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = PlanMeTheme.extendedColors.success.color
                            )
                        }
                    } else {
                        IconButton(
                            enabled = !moveTaskItem.isOwn(moveTaskDialogArgs = moveTaskDialogArgs),
                            onClick = {
                                onAction(
                                    MoveTaskDialogUiAction
                                        .OnPickMoveTaskItem(
                                            moveTaskModel = moveTaskItem
                                        )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (moveTaskItem.isOwn(moveTaskDialogArgs = moveTaskDialogArgs)) {
                                    Icons.Outlined.Circle
                                } else {
                                    Icons.Outlined.SwipeRightAlt
                                },
                                contentDescription = null,
                            )
                        }
                    }
                }
                if (
                    moveTaskModels.lastIndex != index
                ) {
                    HorizontalDivider()
                }
            }

            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }

}