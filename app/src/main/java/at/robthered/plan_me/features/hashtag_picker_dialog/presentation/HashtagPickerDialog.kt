package at.robthered.plan_me.features.hashtag_picker_dialog.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel.FoundHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel.NewHashTagModel
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.HashtagPickerDialogNavigationActions
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.navigation.HashtagPickerDialogNavigationEvent
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.FoundHashtagsFlowRow
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagCardItem
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagCardItemVisibility
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagItemActionIcon
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagsFlowRow
import at.robthered.plan_me.features.ui.presentation.composables.AppAbortDialog
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import at.robthered.plan_me.features.ui.presentation.theme.PlanMeTheme
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HashtagPickerDialogRoot(
    modifier: Modifier = Modifier,
    hashtagPickerDialogNavigationActions: HashtagPickerDialogNavigationActions,
    hashtagPickerDialogViewModel: HashtagPickerDialogViewModel = koinViewModel<HashtagPickerDialogViewModel>(),
) {

    val newHashtags by hashtagPickerDialogViewModel.newHashtags.collectAsStateWithLifecycle()
    val didListChange by hashtagPickerDialogViewModel.didListChange.collectAsStateWithLifecycle()
    val isLoading by hashtagPickerDialogViewModel.isLoading.collectAsStateWithLifecycle()
    val canSaveNewHashtag by hashtagPickerDialogViewModel.canSaveNewHashtag.collectAsStateWithLifecycle()
    val newHashtagModel by hashtagPickerDialogViewModel.newHashtagModel.collectAsStateWithLifecycle()
    val newHashtagModelError by hashtagPickerDialogViewModel.newHashtagModelError.collectAsStateWithLifecycle()
    val foundHashtags by hashtagPickerDialogViewModel.foundHashtags.collectAsStateWithLifecycle()

    ObserveAsEvents(
        flow = hashtagPickerDialogViewModel.appNavigationEvent
    ) { event ->
        when (event) {
            HashtagPickerDialogNavigationEvent.OnNavigateBack -> hashtagPickerDialogNavigationActions.onNavigateBack()
        }
    }

    HashtagPickerDialog(
        modifier = modifier,
        newHashtags = newHashtags,
        didListChange = didListChange,
        isLoading = isLoading,
        canSaveNewHashtag = canSaveNewHashtag,
        newHashtagModel = newHashtagModel,
        newHashtagModelError = newHashtagModelError,
        foundHashtags = foundHashtags,
        onAction = hashtagPickerDialogViewModel::onAction
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HashtagPickerDialog(
    modifier: Modifier = Modifier,
    newHashtags: List<UiHashtagModel>,
    didListChange: Boolean,
    onAction: (action: HashtagPickerDialogUiAction) -> Unit,
    isLoading: Boolean,
    canSaveNewHashtag: Boolean,
    newHashtagModel: NewHashTagModel,
    newHashtagModelError: NewHashtagModelError,
    foundHashtags: List<FoundHashtagModel>,
) {


    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = didListChange,
        onNavigateBack = {
            onAction(
                HashtagPickerDialogUiAction
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
                title = stringResource(R.string.hashtag_dialog_modal_title),
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
            HashtagsFlowRow(
                hashtags = newHashtags
            ) { index, item ->
                val haptics = LocalHapticFeedback.current
                var isHashtagMenuOpen by remember {
                    mutableStateOf(false)
                }
                val backgroundColor by animateColorAsState(
                    targetValue = if (isHashtagMenuOpen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                    animationSpec = tween(),
                    label = "backgroundColor-id-${item.hashCode()}"
                )
                when (item) {
                    is UiHashtagModel.ExistingHashtagModel -> {
                        HashtagCardItem(
                            modifier = Modifier
                                .combinedClickable(
                                    onDoubleClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isHashtagMenuOpen = isHashtagMenuOpen.not()
                                    },
                                    onLongClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isHashtagMenuOpen = isHashtagMenuOpen.not()
                                    },
                                    onClick = {

                                    }
                                ),
                            text = item.name,
                            backgroundColor = backgroundColor
                        ) {
                            HashtagCardItemVisibility(
                                visible = isHashtagMenuOpen,
                                delayMillis = 150
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    HashtagItemActionIcon(
                                        onClick = {
                                            onAction(
                                                HashtagPickerDialogUiAction
                                                    .OnRemoveNewHashtag(index = index)
                                            )
                                            isHashtagMenuOpen = false
                                        },
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tintColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    HashtagItemActionIcon(
                                        onClick = {
                                            isHashtagMenuOpen = false
                                        },
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Close",
                                    )
                                }
                            }
                        }
                    }

                    is NewHashTagModel -> {
                        HashtagCardItem(
                            modifier = Modifier
                                .combinedClickable(
                                    onDoubleClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isHashtagMenuOpen = isHashtagMenuOpen.not()
                                    },
                                    onLongClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isHashtagMenuOpen = isHashtagMenuOpen.not()
                                    },
                                    onClick = {

                                    }
                                ),
                            text = item.name,
                            backgroundColor = if (newHashtagModel.index == item.index) PlanMeTheme.extendedColors.success.colorContainer.copy(
                                alpha = 0.7f
                            ) else backgroundColor,
                            borderColor = if (newHashtagModel.index == item.index) PlanMeTheme.extendedColors.success.color.copy(
                                alpha = 0.5f
                            ) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        ) {
                            HashtagCardItemVisibility(
                                visible = isHashtagMenuOpen,
                                delayMillis = 150
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    HashtagItemActionIcon(
                                        onClick = {
                                            onAction(
                                                HashtagPickerDialogUiAction
                                                    .OnEditNewHashtagItem(
                                                        newHashtagModel = item
                                                    )
                                            )
                                        },
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Edit",
                                    )
                                    HashtagItemActionIcon(
                                        onClick = {
                                            onAction(
                                                HashtagPickerDialogUiAction
                                                    .OnRemoveNewHashtag(index = index)
                                            )
                                            isHashtagMenuOpen = false
                                        },
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete",
                                    )
                                    HashtagItemActionIcon(
                                        onClick = {
                                            isHashtagMenuOpen = false
                                            onAction(
                                                HashtagPickerDialogUiAction
                                                    .OnResetNewHashtagModel
                                            )
                                        },
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Close",
                                    )
                                }
                            }
                        }
                    }

                    is FoundHashtagModel -> Unit
                }
            }

            FoundHashtagsFlowRow(
                modifier = Modifier,
                foundHashtags = if (newHashtagModel.index != null) emptyList() else foundHashtags,
                onSaveExistingHashtag = {
                    it
                    onAction(
                        HashtagPickerDialogUiAction
                            .OnAddExistingHashtag(
                                foundHashtagModel = FoundHashtagModel(
                                    hashtagId = it.hashtagId,
                                    name = it.name
                                )
                            )
                    )
                },
                onResetState = {
                    onAction(
                        HashtagPickerDialogUiAction
                            .OnResetNewHashtagModel
                    )
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
                    value = newHashtagModel.name,
                    focusRequester = name,
                    prefix = {
                        Icon(
                            imageVector = Icons.Outlined.Tag,
                            contentDescription = null
                        )
                    },
                    onValueChange = {
                        onAction(
                            HashtagPickerDialogUiAction
                                .OnChangeNewHashtagName(
                                    name = it
                                )
                        )
                    },
                    singleLine = true,
                    error = newHashtagModelError.name?.toUiText(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSaveNewHashtag) {
                                onAction(
                                    HashtagPickerDialogUiAction
                                        .OnSaveNewHashtag
                                )
                            }
                        }
                    ),
                    placeholder = stringResource(id = R.string.hashtag_name_placeholder),
                    label = if (newHashtagModel.index != null) {
                        stringResource(id = R.string.hashtag_name_label_edit)
                    } else {
                        stringResource(id = R.string.hashtag_name_label)
                    }
                )
                FilledTonalIconButton(
                    enabled = canSaveNewHashtag,
                    onClick = {
                        onAction(
                            HashtagPickerDialogUiAction
                                .OnSaveNewHashtag
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null
                    )
                }
            }
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
                                    HashtagPickerDialogUiAction
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
                    enabled = !isLoading && newHashtags.isNotEmpty(),
                    onClick = {
                        onAction(
                            HashtagPickerDialogUiAction
                                .OnSaveHashtags
                        )
                    }
                ) {
                    Text(
                        text = if (isLoading)
                            stringResource(R.string.add_task_dialog_saving_text)
                        else
                            stringResource(R.string.add_task_dialog_save_text)
                    )
                }
            }
        }
    }

}