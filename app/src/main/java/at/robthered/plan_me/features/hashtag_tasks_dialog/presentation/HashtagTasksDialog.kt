package at.robthered.plan_me.features.hashtag_tasks_dialog.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.LinkOff
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.HashtagTasksDialogAppResourceError
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.asAnnotatedString
import at.robthered.plan_me.features.common.presentation.asString
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.common.presentation.icons.TaskDescription
import at.robthered.plan_me.features.common.presentation.navigation.TaskDetailsDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateHashtagNameDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.UpdateTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.remember.appSheetState.rememberAppSheetState
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import at.robthered.plan_me.features.data_source.presentation.ext.toUiText
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.HashtagTasksDialogNavigationActions
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.HashtagTasksDialogNavigationEvent
import at.robthered.plan_me.features.inbox_screen.presentation.composables.stampOverlay
import at.robthered.plan_me.features.ui.presentation.composables.AppModalTitle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HashtagTasksDialogRoot(
    modifier: Modifier = Modifier,
    hashtagTasksDialogNavigationActions: HashtagTasksDialogNavigationActions,
    hashtagTasksDialogViewModel: HashtagTasksDialogViewModel = koinViewModel<HashtagTasksDialogViewModel>(),
) {

    val hashtagTasksResource by hashtagTasksDialogViewModel.hashtagTasksResource.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = hashtagTasksDialogViewModel.appNavigationEvent) { event ->
        when (event) {
            HashtagTasksDialogNavigationEvent.OnNavigateBack -> hashtagTasksDialogNavigationActions.onNavigateBack()
            is HashtagTasksDialogNavigationEvent.OnNavigateToTaskDetailsDialog ->
                hashtagTasksDialogNavigationActions
                    .onNavigateToTaskDetailsDialog(args = event.args)

            is HashtagTasksDialogNavigationEvent.OnNavigateToUpdateHashtagNameDialog ->
                hashtagTasksDialogNavigationActions
                    .onNavigateToUpdateHashtagNameDialog(
                        args = event.args
                    )

            is HashtagTasksDialogNavigationEvent.OnNavigateToUpdateTaskDialog ->
                hashtagTasksDialogNavigationActions
                    .onNavigateToUpdateTaskDialog(
                        args = event.args
                    )
        }
    }


    HashtagTasksDialog(
        modifier = modifier,
        hashtagTasksResource = hashtagTasksResource,
        onAction = hashtagTasksDialogViewModel::onAction,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HashtagTasksDialog(
    modifier: Modifier = Modifier,
    hashtagTasksResource: AppResource<HashtagWithTasksModel>,
    onAction: (action: HashtagTasksDialogUiActions) -> Unit,
) {


    val appSheetState = rememberAppSheetState(
        shouldShowConfirmationDialog = false,
        onNavigateBack = {
            onAction(
                HashtagTasksDialogUiActions.OnNavigateBack
            )
        }
    )
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
            when (hashtagTasksResource) {
                is AppResource.Error -> {
                    if (hashtagTasksResource.error is HashtagTasksDialogAppResourceError) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hashtagTasksResource.error.toUiText().asString()
                                )
                            }
                        }
                    }

                }

                is AppResource.Loading -> {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                AppResource.Stale -> Unit
                is AppResource.Success -> {
                    item {
                        val title = UiText
                            .StringResource(
                                id = R.string.hashtag_tasks_dialog_modal_title,
                                args = listOf("#${hashtagTasksResource.data.hashtag.name}")
                            ).asAnnotatedString(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        AppModalTitle(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            title = title,
                            leadingIcon = {
                                IconButton(
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    onClick = {
                                        onAction(
                                            HashtagTasksDialogUiActions
                                                .OnNavigateToUpdateHashtagNameDialog(
                                                    args = UpdateHashtagNameDialogArgs(
                                                        hashtagId = hashtagTasksResource.data.hashtag.hashtagId
                                                    )
                                                )
                                        )
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.7f
                                        ),
                                        contentDescription = null
                                    )
                                }
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
                    }
                    itemsIndexed(
                        items = hashtagTasksResource.data.tasks
                    ) { index, taskModel ->
                        val cardAlpha by animateFloatAsState(
                            targetValue = if (taskModel.isCompleted) {
                                0.2f
                            } else 1f,
                            animationSpec = tween(durationMillis = 450),
                            label = "${taskModel.isCompleted}-card-alpha"
                        )
                        val crossStrokeColor by animateColorAsState(
                            targetValue = if (taskModel.isCompleted) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
                            },
                            label = "${taskModel.isCompleted}-cross-stroke-color",
                            animationSpec = tween(durationMillis = 450)
                        )
                        var isDescriptionCurrentlyExpanded by remember(taskModel.taskId) {
                            mutableStateOf(false)
                        }
                        var descriptionIsActuallyOverflowing by remember(taskModel.description) {
                            mutableStateOf(false)
                        }
                        val showDescriptionExpandIcon =
                            descriptionIsActuallyOverflowing || isDescriptionCurrentlyExpanded

                        var isDescriptionExpanded by remember(taskModel.taskId) {
                            mutableStateOf(false)
                        }
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onAction(
                                        HashtagTasksDialogUiActions
                                            .OnNavigateToTaskDetailsDialog(
                                                args = TaskDetailsDialogArgs(
                                                    taskId = taskModel.taskId
                                                )
                                            )
                                    )
                                }
                                .height(IntrinsicSize.Min)
                                .then(
                                    if (taskModel.isArchived) {
                                        Modifier.stampOverlay()
                                    } else {
                                        Modifier
                                    }
                                )
                                .alpha(cardAlpha)
                                .heightIn(min = 48.dp)
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .drawBehind {
                                    drawLine(
                                        start = Offset(
                                            x = 24f,
                                            y = 12f,
                                        ),
                                        end = Offset(
                                            x = this.size.width - 24f,
                                            y = this.size.height - 10f,
                                        ),
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round,
                                        color = crossStrokeColor
                                    )
                                    drawLine(
                                        start = Offset(
                                            x = 24f,
                                            y = this.size.height - 12f,
                                        ),
                                        end = Offset(
                                            x = this.size.width - 24f,
                                            y = 12f,
                                        ),
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round,
                                        color = crossStrokeColor
                                    )
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .clickable {

                                            onAction(
                                                HashtagTasksDialogUiActions
                                                    .OnToggleCompleteTask(
                                                        taskId = taskModel.taskId,
                                                        title = taskModel.title,
                                                        isCompleted = taskModel.isCompleted
                                                    )
                                            )
                                        },
                                    imageVector = if (taskModel.isCompleted) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Circle,
                                    contentDescription = if (taskModel.isCompleted) "Task is completed - Icon" else "Task is not completed - Icon",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                if (showDescriptionExpandIcon) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    val rotationDegreeDescription by animateFloatAsState(
                                        targetValue = if (isDescriptionCurrentlyExpanded) 180f else 0f,
                                        animationSpec = tween(durationMillis = 200),
                                        label = "DescriptionExpandIconRotation"
                                    )
                                    Icon(
                                        modifier = Modifier
                                            .rotate(rotationDegreeDescription)
                                            .clickable {
                                                isDescriptionCurrentlyExpanded =
                                                    !isDescriptionCurrentlyExpanded
                                            },
                                        imageVector = Icons.Outlined.ExpandMore,
                                        contentDescription = if (isDescriptionCurrentlyExpanded) "Collapse description" else "Expand description",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                                    .animateContentSize(),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    text = taskModel.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                taskModel.description?.let {
                                    Text(
                                        modifier = Modifier,
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = if (isDescriptionCurrentlyExpanded) Int.MAX_VALUE else 2,
                                        overflow = if (isDescriptionExpanded) {
                                            TextOverflow.Visible
                                        } else {
                                            TextOverflow.Ellipsis
                                        },
                                        onTextLayout = { textLayoutResult ->
                                            if (!isDescriptionCurrentlyExpanded) {
                                                descriptionIsActuallyOverflowing =
                                                    textLayoutResult.hasVisualOverflow
                                            }
                                        }
                                    )
                                } ?: run {
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                onAction(
                                                    HashtagTasksDialogUiActions
                                                        .OnNavigateToUpdateTaskDialog(
                                                            args = UpdateTaskDialogArgs(
                                                                taskId = taskModel.taskId
                                                            )
                                                        )
                                                )
                                            }
                                    ) {
                                        Icon(
                                            imageVector = TaskDescription,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                        )
                                        Text(
                                            text = "No description",
                                            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                        )
                                    }
                                }
                            }
                            IconButton(
                                onClick = {
                                    onAction(
                                        HashtagTasksDialogUiActions
                                            .OnDeleteTaskHashtagReference(
                                                taskId = taskModel.taskId
                                            )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LinkOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                        }
                        if (index != -1) {
                            HorizontalDivider(
                                thickness = 1.dp,
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(96.dp))
                    }
                }
            }

        }
    }


}