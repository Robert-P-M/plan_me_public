package at.robthered.plan_me.features.add_section_dialog.presentation

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.AddSectionDialogNavigationActions
import at.robthered.plan_me.features.add_section_dialog.presentation.navigation.AddSectionDialogNavigationEvent
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModel
import at.robthered.plan_me.features.data_source.domain.model.add_section.AddSectionModelError
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddSectionDialogRoot(
    modifier: Modifier = Modifier,
    addSectionDialogViewModel: AddSectionDialogViewModel = koinViewModel<AddSectionDialogViewModel>(),
    addSectionDialogNavigationActions: AddSectionDialogNavigationActions,
) {
    val addSectionModel by addSectionDialogViewModel.addSectionModel.collectAsStateWithLifecycle()
    val addSectionModelError by addSectionDialogViewModel.addSectionModelError.collectAsStateWithLifecycle()
    val didModelChange by addSectionDialogViewModel.didModelChange.collectAsStateWithLifecycle()
    val canSave by addSectionDialogViewModel.canSave.collectAsStateWithLifecycle()
    val isLoading by addSectionDialogViewModel.isLoading.collectAsStateWithLifecycle()


    ObserveAsEvents(addSectionDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            AddSectionDialogNavigationEvent.OnNavigateBack -> {
                addSectionDialogNavigationActions.onNavigateBack()
            }
        }
    }

    AddSectionDialog(
        modifier = modifier,
        addSectionModel = addSectionModel,
        addSectionModelError = addSectionModelError,
        didModelChange = didModelChange,
        canSave = canSave,
        isLoading = isLoading,
        onAction = addSectionDialogViewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSectionDialog(
    modifier: Modifier = Modifier,
    addSectionModel: AddSectionModel,
    addSectionModelError: AddSectionModelError,
    didModelChange: Boolean,
    canSave: Boolean,
    onAction: (action: AddSectionDialogAction) -> Unit,
    isLoading: Boolean,
) {

    val (title) = remember { FocusRequester.createRefs() }
    val listState = rememberLazyListState()

    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didModelChange,
        onNavigateBack = {
            onAction(
                AddSectionDialogAction.OnNavigateBack
            )
        }
    )

    val abortDialogText = UiText.StringResource(
        id = R.string.add_task_dialog_confirm_back_navigation_text,
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
        dragHandle = null
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
                    title = stringResource(R.string.add_section_dialog_modal_title),
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
                    value = addSectionModel.title,
                    onValueChange = {
                        onAction(
                            AddSectionDialogAction
                                .OnChangeTitle(it)
                        )
                    },
                    error = addSectionModelError.title?.toUiText(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {

                            if (canSave) {
                                onAction(
                                    AddSectionDialogAction
                                        .OnSaveSection
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
                                        AddSectionDialogAction
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
                            onAction(
                                AddSectionDialogAction
                                    .OnSaveSection
                            )
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