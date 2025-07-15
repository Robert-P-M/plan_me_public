package at.robthered.plan_me.features.update_section_title_dialog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModelError
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.UpdateSectionTitleDialogNavigationActions
import at.robthered.plan_me.features.update_section_title_dialog.presentation.navigation.UpdateSectionTitleDialogNavigationEvent
import org.koin.androidx.compose.koinViewModel


@Composable
fun UpdateSectionTitleDialogRoot(
    modifier: Modifier = Modifier,
    updateSectionTitleDialogViewModel: UpdateSectionTitleDialogViewModel = koinViewModel<UpdateSectionTitleDialogViewModel>(),
    updateSectionTitleDialogNavigationActions: UpdateSectionTitleDialogNavigationActions,
) {
    val updateSectionTitleModel by updateSectionTitleDialogViewModel.updateSectionTitleModel.collectAsStateWithLifecycle()
    val didModelChange by updateSectionTitleDialogViewModel.didModelChange.collectAsStateWithLifecycle()
    val isLoading by updateSectionTitleDialogViewModel.isLoading.collectAsStateWithLifecycle()
    val canSave by updateSectionTitleDialogViewModel.canSave.collectAsStateWithLifecycle()
    val updateSectionTitleModelError by updateSectionTitleDialogViewModel.updateSectionTitleModelError.collectAsStateWithLifecycle()

    ObserveAsEvents(updateSectionTitleDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            UpdateSectionTitleDialogNavigationEvent.OnNavigateBack -> {
                updateSectionTitleDialogNavigationActions.onNavigateBack()
            }
        }
    }

    UpdateSectionTitleDialog(
        modifier = modifier,
        updateSectionTitleModel = updateSectionTitleModel,
        didModelChange = didModelChange,
        isLoading = isLoading,
        updateSectionTitleModelError = updateSectionTitleModelError,
        canSave = canSave,
        onAction = updateSectionTitleDialogViewModel::onAction
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateSectionTitleDialog(
    modifier: Modifier = Modifier,
    updateSectionTitleModel: UpdateSectionTitleModel,
    didModelChange: Boolean,
    onAction: (action: UpdateSectionTitleDialogUiAction) -> Unit,
    isLoading: Boolean,
    updateSectionTitleModelError: UpdateSectionTitleModelError,
    canSave: Boolean,
) {

    val listState = rememberLazyListState()
    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didModelChange,
        onNavigateBack = {
            onAction(
                UpdateSectionTitleDialogUiAction.OnNavigateBack
            )
        }
    )

    val abortDialogText = UiText.StringResource(
        id = R.string.edit_section_title_dialog_confirm_back_navigation_text,
        args = listOf(stringResource(R.string.edit_section_title_dialog_confirm_back_navigation_text_lost))
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
                    title = stringResource(R.string.edit_section_title_dialog_modal_title),
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = updateSectionTitleModel.title,
                    onValueChange = {
                        onAction(
                            UpdateSectionTitleDialogUiAction
                                .OnChangeTitle(
                                    it
                                )
                        )
                    },
                    error = updateSectionTitleModelError.title?.toUiText(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSave) {
                                onAction(
                                    UpdateSectionTitleDialogUiAction
                                        .OnUpdateSection
                                )
                            }
                        }
                    ),
                    placeholder = stringResource(id = R.string.add_section_dialog_section_title_placeholder),
                    label = stringResource(R.string.add_section_dialog_section_title_label)
                )
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
                            text = stringResource(R.string.add_task_dialog_cancel_text)
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
                                        UpdateSectionTitleDialogUiAction
                                            .OnResetState
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = stringResource(R.string.add_task_dialog_reset_input_icon_description)
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
                            if (canSave) {
                                onAction(
                                    UpdateSectionTitleDialogUiAction
                                        .OnUpdateSection
                                )
                            }
                        }
                    ) {
                        Text(
                            text = if (isLoading)
                                stringResource(R.string.add_section_dialog_saving_text)
                            else
                                stringResource(
                                    R.string.add_section_dialog_save_text
                                )
                        )
                    }
                }

            }
        }
    }

}