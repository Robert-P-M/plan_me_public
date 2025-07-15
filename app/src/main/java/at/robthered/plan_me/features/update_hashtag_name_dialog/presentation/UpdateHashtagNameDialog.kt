package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.UpdateHashtagNameDialogNavigationActions
import at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.navigation.UpdateHashtagNameDialogNavigationEvent
import org.koin.androidx.compose.koinViewModel


@Composable
fun UpdateHashtagNameDialogRoot(
    modifier: Modifier = Modifier,
    updateHashtagNameDialogNavigationActions: UpdateHashtagNameDialogNavigationActions,
    updateHashtagNameDialogViewModel: UpdateHashtagNameDialogViewModel = koinViewModel<UpdateHashtagNameDialogViewModel>(),
) {

    val updateHashtagModel by updateHashtagNameDialogViewModel.updateHashtagModel.collectAsStateWithLifecycle()
    val updateHashtagModelError by updateHashtagNameDialogViewModel.updateHashtagModelError.collectAsStateWithLifecycle()
    val didUpdateModelChange by updateHashtagNameDialogViewModel.didUpdateModelChange.collectAsStateWithLifecycle()
    val canSaveUpdateHashtag by updateHashtagNameDialogViewModel.canSaveUpdateHashtag.collectAsStateWithLifecycle()

    ObserveAsEvents(
        flow = updateHashtagNameDialogViewModel.appNavigationEvent
    ) { event ->
        when (event) {
            UpdateHashtagNameDialogNavigationEvent.OnNavigateBack -> updateHashtagNameDialogNavigationActions.onNavigateBack()
        }
    }

    UpdateHashtagNameDialog(
        modifier = modifier,
        updateHashtagModel = updateHashtagModel,
        updateHashtagModelError = updateHashtagModelError,
        didUpdateModelChange = didUpdateModelChange,
        canSaveUpdateHashtag = canSaveUpdateHashtag,
        onAction = updateHashtagNameDialogViewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateHashtagNameDialog(
    modifier: Modifier = Modifier,
    updateHashtagModel: UpdateHashtagModel,
    updateHashtagModelError: UpdateHashtagModelError,
    didUpdateModelChange: Boolean,
    canSaveUpdateHashtag: Boolean,
    onAction: (UpdateHashtagNameDialogUiAction) -> Unit,
) {


    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didUpdateModelChange,
        onNavigateBack = {
            onAction(
                UpdateHashtagNameDialogUiAction
                    .OnNavigateBack
            )
        },
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
        Column {
            AppModalTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                title = stringResource(R.string.update_hashtag_name_dialog_modal_title),
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
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val (name) = remember { FocusRequester.createRefs() }
                AppOutlinedTextField(
                    modifier = Modifier
                        .weight(1f),
                    value = updateHashtagModel.name,
                    focusRequester = name,
                    prefix = {
                        Icon(
                            imageVector = Icons.Outlined.Tag,
                            contentDescription = null
                        )
                    },
                    onValueChange = {
                        onAction(
                            UpdateHashtagNameDialogUiAction
                                .OnChangeName(
                                    name = it
                                )
                        )
                    },
                    singleLine = true,
                    error = updateHashtagModelError.name?.toUiText(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSaveUpdateHashtag) {
                                onAction(
                                    UpdateHashtagNameDialogUiAction
                                        .OnSaveHashtag
                                )
                            }
                        }
                    ),
                    placeholder = stringResource(id = R.string.hashtag_name_placeholder),
                    label = stringResource(id = R.string.hashtag_name_label)
                )
                FilledTonalIconButton(
                    enabled = canSaveUpdateHashtag,
                    onClick = {
                        onAction(
                            UpdateHashtagNameDialogUiAction
                                .OnSaveHashtag
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null
                    )
                }
            }
        }
    }


}