package at.robthered.plan_me.features.update_task_dialog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModelError
import at.robthered.plan_me.features.data_source.presentation.ext.model.imageVector
import at.robthered.plan_me.features.data_source.presentation.ext.model.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.UpdateTaskDialogNavigationActions
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.UpdateTaskDialogNavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpdateTaskDialogRoot(
    modifier: Modifier = Modifier,
    updateTaskDialogNavigationActions: UpdateTaskDialogNavigationActions,
    updateTaskDialogViewModel: UpdateTaskDialogViewModel = koinViewModel<UpdateTaskDialogViewModel>(),
) {


    val updateTaskModel by updateTaskDialogViewModel.updateTaskModel.collectAsState()
    val updateTaskModelError by updateTaskDialogViewModel.updateTaskModelError.collectAsState()
    val didModelChange by updateTaskDialogViewModel.didModelChange.collectAsState()
    val canSave by updateTaskDialogViewModel.canSave.collectAsState()
    val isLoading by updateTaskDialogViewModel.isLoading.collectAsState()

    ObserveAsEvents(updateTaskDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            UpdateTaskDialogNavigationEvent.OnNavigateBack -> {
                updateTaskDialogNavigationActions.onNavigateBack()
            }

            UpdateTaskDialogNavigationEvent.OnNavigateToPriorityPickerDialog -> {
                updateTaskDialogNavigationActions.onNavigateToPriorityPickerDialog()
            }
        }
    }

    UpdateTaskDialog(
        modifier = modifier,
        updateTaskModel = updateTaskModel,
        onAction = updateTaskDialogViewModel::onAction,
        updateTaskModelError = updateTaskModelError,
        didModelChange = didModelChange,
        canSave = canSave,
        isLoading = isLoading,
    )


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpdateTaskDialog(
    modifier: Modifier = Modifier,
    updateTaskModel: UpdateTaskModel,
    onAction: (action: UpdateTaskDialogUiAction) -> Unit,
    updateTaskModelError: UpdateTaskModelError,
    didModelChange: Boolean,
    canSave: Boolean,
    isLoading: Boolean,
) {
    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didModelChange,
        onNavigateBack = {
            onAction(
                UpdateTaskDialogUiAction
                    .OnNavigateBack
            )
        }
    )

    val (title, description) = remember { FocusRequester.createRefs() }
    val listState = rememberLazyListState()

    val abortDialogText = UiText.StringResource(
        id = R.string.update_task_dialog_confirm_back_navigation_text,
        args = listOf(stringResource(R.string.add_task_dialog_confirm_back_navigation_text_lost))
    )

    if (appSheetState.showConfirmationDialog) {
        AppAbortDialog(
            onDismissRequest = {
                appSheetState.cancelDiscardChanges()
            },
            onAccept = {
                appSheetState.confirmDiscardAndNavigateBack()
            },
            title = stringResource(R.string.add_task_dialog_confirm_back_navigation_title),
            text = abortDialogText.asAnnotatedString(color = MaterialTheme.colorScheme.error)
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
        LazyColumn(
            modifier = Modifier,
            state = listState,
        ) {
            item {
                AppModalTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    title = stringResource(R.string.update_task_dialog_modal_title),
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
            item {
                AppOutlinedTextField(
                    modifier = Modifier
                        .focusRequester(title)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = updateTaskModel.title,
                    onValueChange = {
                        onAction(
                            UpdateTaskDialogUiAction
                                .OnChangeTitle(it)
                        )
                    },
                    error = updateTaskModelError.title?.toUiText(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            description.requestFocus()
                        }
                    ),
                    placeholder = stringResource(id = R.string.add_task_dialog_task_title_placeholder),
                    label = stringResource(R.string.add_task_dialog_task_title_label)
                )
            }
            item {
                AppOutlinedTextField(
                    modifier = Modifier
                        .focusRequester(description)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = updateTaskModel.description ?: "",
                    onValueChange = {
                        if (it.isNotEmpty()) {
                            onAction(
                                UpdateTaskDialogUiAction
                                    .OnChangeDescription(it)
                            )
                        } else {
                            onAction(
                                UpdateTaskDialogUiAction
                                    .OnChangeDescription(null)
                            )
                        }

                    },
                    singleLine = false,
                    error = updateTaskModelError.description?.toUiText(),
                    keyboardOptions = KeyboardOptions.Default,
                    keyboardActions = KeyboardActions(),
                    placeholder = stringResource(id = R.string.add_task_dialog_task_description_placeholder),
                    label = stringResource(id = R.string.add_task_dialog_task_description_label),
                )
            }
            item {
                val rowState = rememberLazyListState()
                val flingBehaviour =
                    rememberSnapFlingBehavior(
                        lazyListState = rowState,
                        snapPosition = SnapPosition.Center
                    )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Options"
                    )
                    LazyRow(
                        state = rowState,
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        flingBehavior = flingBehaviour,
                    ) {
                        item {
                            ToggleButton(
                                modifier = Modifier,
                                checked = updateTaskModel.priorityEnum != PriorityEnum.NORMAL,
                                onCheckedChange = {
                                    onAction(
                                        UpdateTaskDialogUiAction
                                            .OnNavigateToPriorityPickerDialog
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = updateTaskModel.priorityEnum.imageVector(),
                                    contentDescription = updateTaskModel.priorityEnum.toUiText(short = false)
                                        .asString(),
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = updateTaskModel.priorityEnum.toUiText(short = false)
                                        .asString(),
                                )
                            }
                        }
                    }
                }

            }
            item {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        enabled = !isLoading,
                        onClick = {
                            appSheetState.requestHide()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.update_task_dialog_cancel_text)
                        )
                    }
                    AnimatedContent(targetState = isLoading) { loading ->
                        if (loading) {
                            CircularProgressIndicator()
                        } else {
                            FilledTonalIconButton(
                                modifier = Modifier
                                    .wrapContentSize(align = Alignment.Center),
                                enabled = !isLoading,
                                onClick = {
                                    onAction(
                                        UpdateTaskDialogUiAction
                                            .OnResetState
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = stringResource(R.string.update_task_dialog_reset_input_icon_description)
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(IntrinsicSize.Min),
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        enabled = canSave && !isLoading,
                        onClick = {
                            onAction(
                                UpdateTaskDialogUiAction
                                    .OnUpdateTask
                            )
                        }
                    ) {
                        Text(
                            text = if (isLoading)
                                stringResource(R.string.update_task_dialog_save_text)
                            else
                                stringResource(R.string.update_task_dialog_save_text)
                        )
                    }
                }
            }

        }
    }


}