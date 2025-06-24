package at.robthered.plan_me.features.task_hashtags_dialog.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.navigation.HashtagTasksDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel.FoundHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel.NewHashTagModel
import at.robthered.plan_me.features.data_source.presentation.ext.validation.toUiText
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.FoundHashtagsFlowRow
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagCardItem
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagCardItemVisibility
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagItemActionIcon
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.composables.HashtagsFlowRow
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.TaskHashtagsDialogNavigationActions
import at.robthered.plan_me.features.task_hashtags_dialog.presentation.navigation.TaskHashtagsDialogNavigationEvent
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import at.robthered.plan_me.features.ui.presentation.composables.AppOutlinedTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TaskHashtagsDialogRoot(
    modifier: Modifier = Modifier,
    taskHashtagsNavigationActions: TaskHashtagsDialogNavigationActions,
    taskHashtagsDialogViewModel: TaskHashtagsDialogViewModel = koinViewModel<TaskHashtagsDialogViewModel>(),
) {

    val taskHashtags by taskHashtagsDialogViewModel.taskHashtags.collectAsStateWithLifecycle()
    val foundHashtags by taskHashtagsDialogViewModel.foundHashtags.collectAsStateWithLifecycle()
    val newHashtagModel by taskHashtagsDialogViewModel.newHashtagModel.collectAsStateWithLifecycle()
    val newHashtagModelError by taskHashtagsDialogViewModel.newHashtagModelError.collectAsStateWithLifecycle()
    val canSaveNewHashtag by taskHashtagsDialogViewModel.canSaveNewHashtag.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = taskHashtagsDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            TaskHashtagsDialogNavigationEvent.OnNavigateBack ->
                taskHashtagsNavigationActions.onNavigateBack()

            is TaskHashtagsDialogNavigationEvent.OnNavigateToUpdateHashtagNameDialog ->
                taskHashtagsNavigationActions.onNavigateToUpdateHashtagNameDialog(
                    args = event.args
                )

            is TaskHashtagsDialogNavigationEvent.OnNavigateToHashtagTasksDialog ->
                taskHashtagsNavigationActions.onNavigateToHashtagTasksDialog(
                    args = event.args
                )
        }
    }



    TaskHashtagsDialog(
        modifier = modifier,
        taskHashtags = taskHashtags,
        foundHashtags = foundHashtags,
        newHashtagModel = newHashtagModel,
        newHashtagModelError = newHashtagModelError,
        canSaveNewHashtag = canSaveNewHashtag,
        onAction = taskHashtagsDialogViewModel::onAction
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskHashtagsDialog(
    modifier: Modifier = Modifier,
    taskHashtags: List<HashtagModel>,
    onAction: (TaskHashtagsDialogUiAction) -> Unit,
    newHashtagModel: NewHashTagModel,
    newHashtagModelError: NewHashtagModelError,
    canSaveNewHashtag: Boolean,
    foundHashtags: List<FoundHashtagModel>,
) {
    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = false,
        onNavigateBack = {
            onAction(
                TaskHashtagsDialogUiAction
                    .OnNavigateBack
            )
        },
    )

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            appSheetState.requestHide()
        },
        sheetState = appSheetState.sheetState,
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            var isInputVisible by remember {
                mutableStateOf(false)
            }
            AppModalTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                title = stringResource(R.string.task_hashtags_dialog_modal_title),
                leadingIcon = {
                    HashtagItemActionIcon(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            isInputVisible = isInputVisible.not()
                        },
                        imageVector = if (isInputVisible) Icons.Outlined.Close else Icons.Outlined.AddCircleOutline,
                        contentDescription = null,
                        tintColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
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
                hashtags = taskHashtags,
            ) { index, item ->
                val haptics = LocalHapticFeedback.current
                var isHashtagMenuOpen by remember {
                    mutableStateOf(false)
                }
                HashtagCardItemVisibility(
                    visible = true,
                    delayMillis = 50 + (index * 30)
                ) {
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isHashtagMenuOpen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                        animationSpec = tween(),
                        label = "backgroundColor-id-${item.hashtagId}"
                    )
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
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    isHashtagMenuOpen = true
                                }
                            ),
                        backgroundColor = backgroundColor,
                        text = item.name
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
                                            TaskHashtagsDialogUiAction
                                                .OnNavigateToUpdateHashtagNameDialog(
                                                    args = UpdateHashtagNameDialogArgs(
                                                        hashtagId = item.hashtagId
                                                    )
                                                )
                                        )

                                        isHashtagMenuOpen = false
                                    },
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit",
                                )
                                HashtagItemActionIcon(
                                    onClick = {
                                        onAction(
                                            TaskHashtagsDialogUiAction
                                                .OnNavigateToHashtagTasksDialog(
                                                    args = HashtagTasksDialogArgs(
                                                        hashtagId = item.hashtagId
                                                    )
                                                )
                                        )
                                        isHashtagMenuOpen = false
                                    },
                                    imageVector = Icons.Outlined.RemoveRedEye,
                                    contentDescription = "View",
                                )
                                HashtagItemActionIcon(
                                    onClick = {
                                        onAction(
                                            TaskHashtagsDialogUiAction
                                                .OnDeleteTaskHashtagReference(
                                                    hashtagId = item.hashtagId,
                                                )
                                        )
                                        isHashtagMenuOpen = false
                                    },
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
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

            }
            FoundHashtagsFlowRow(
                modifier = Modifier,
                foundHashtags = foundHashtags,
                onSaveExistingHashtag = { existingHashtagModel ->
                    onAction(
                        TaskHashtagsDialogUiAction
                            .OnSaveExistingHashtag(
                                existingHashtagModel = existingHashtagModel
                            )
                    )
                },
                onResetState = {
                    onAction(
                        TaskHashtagsDialogUiAction
                            .OnResetState
                    )
                }
            )
            AnimatedVisibility(
                visible = isInputVisible,
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
                                TaskHashtagsDialogUiAction
                                    .OnChangeHashtagName(
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
                                        TaskHashtagsDialogUiAction
                                            .OnSaveNewHashtag
                                    )
                                }
                            }
                        ),
                        placeholder = stringResource(id = R.string.hashtag_name_placeholder),
                        label = stringResource(id = R.string.hashtag_name_label)
                    )
                    FilledTonalIconButton(
                        enabled = canSaveNewHashtag,
                        onClick = {
                            onAction(
                                TaskHashtagsDialogUiAction
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


                Spacer(
                    modifier = Modifier.padding(vertical = 48.dp)
                )
            }


            Spacer(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }

}