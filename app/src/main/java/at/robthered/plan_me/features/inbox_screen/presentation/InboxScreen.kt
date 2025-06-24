package at.robthered.plan_me.features.inbox_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.main_scaffold.MainScaffoldStateAction
import at.robthered.plan_me.features.common.presentation.AppScaffoldStateManagerImpl
import at.robthered.plan_me.features.common.presentation.helper.ObserveAsEvents
import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.view_type.ViewTypeEnum
import at.robthered.plan_me.features.inbox_screen.presentation.composables.BoardViewContent
import at.robthered.plan_me.features.inbox_screen.presentation.composables.InboxFloatingActionButtonMenuItems
import at.robthered.plan_me.features.inbox_screen.presentation.composables.InboxTopAppBar
import at.robthered.plan_me.features.inbox_screen.presentation.composables.ListViewContent
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.InboxScreenNavigationActions
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.InboxScreenNavigationEvent
import at.robthered.plan_me.features.ui.presentation.composables.AppFloatingActionMenuButton
import org.koin.androidx.compose.koinViewModel


@Composable
fun InboxScreenRoot(
    modifier: Modifier = Modifier,
    scaffoldState: AppScaffoldStateManagerImpl,
    inboxScreenViewModel: InboxScreenViewModel = koinViewModel<InboxScreenViewModel>(),
    inboxScreenNavigationActions: InboxScreenNavigationActions,
) {
    val currentViewType by inboxScreenViewModel.currentViewType.collectAsStateWithLifecycle()
    val expandedTaskIds by inboxScreenViewModel.expandedTaskIds.collectAsStateWithLifecycle()
    val expandedSectionIds by inboxScreenViewModel.expandedSectionIds.collectAsStateWithLifecycle()
    val isLoading by inboxScreenViewModel.isLoading.collectAsStateWithLifecycle()
    val showCompleted by inboxScreenViewModel.showCompleted.collectAsStateWithLifecycle()
    val showArchived by inboxScreenViewModel.showArchived.collectAsStateWithLifecycle()
    val sortDirection by inboxScreenViewModel.sortDirection.collectAsStateWithLifecycle()
    val inboxScreenItemsResource by inboxScreenViewModel.inboxScreenItemsResource.collectAsStateWithLifecycle()


    ObserveAsEvents(flow = inboxScreenViewModel.appNavigationEvent) { event ->
        when (event) {
            InboxScreenNavigationEvent.OnNavigateToAddSectionDialog -> {
                inboxScreenNavigationActions.onNavigateToAddSectionDialog()
            }

            is InboxScreenNavigationEvent.OnNavigateToAddTaskDialog -> {
                inboxScreenNavigationActions.onNavigateToAddTaskDialog(
                    args = event.args
                )
            }

            InboxScreenNavigationEvent.OnNavigateToPriorityPickerDialog -> {
                inboxScreenNavigationActions.onNavigateToPriorityPickerDialog()
            }

            is InboxScreenNavigationEvent.OnNavigateToTaskDetailsDialog -> {
                inboxScreenNavigationActions.onNavigateToTaskDetailsDialog(
                    args = event.args
                )
            }

            is InboxScreenNavigationEvent.OnNavigateToUpdateSectionTitleDialog -> {
                inboxScreenNavigationActions.onNavigateToUpdateSectionTitleDialog(
                    args = event.args,
                )
            }

            is InboxScreenNavigationEvent.OnNavigateToUpdateTaskDialog -> {
                inboxScreenNavigationActions.onNavigateToUpdateTaskDialog(args = event.args)
            }

            is InboxScreenNavigationEvent.OnNavigateToTaskStatisticsDialog ->
                inboxScreenNavigationActions.onNavigateToTaskStatisticsDialog(args = event.args)

            is InboxScreenNavigationEvent.OnNavigateToTaskHashtagsDialog ->
                inboxScreenNavigationActions.onNavigateToTaskHashtagsDialog(args = event.args)

            is InboxScreenNavigationEvent.OnNavigateToMoveTaskDialog ->
                inboxScreenNavigationActions.onNavigateToMoveTaskDialog(args = event.args)
        }
    }


    InboxScreen(
        modifier = modifier,
        handleScaffoldAction = scaffoldState::handleAction,
        currentViewType = currentViewType,
        onAction = inboxScreenViewModel::onAction,
        getIsTaskExpanded = { taskId ->
            expandedTaskIds.contains(taskId)
        },
        getIsSectionExpanded = { sectionId ->
            expandedSectionIds.contains(sectionId)
        },
        isLoading = isLoading,
        inboxScreenNavigationActions = inboxScreenNavigationActions,
        showCompleted = showCompleted,
        showArchived = showArchived,
        sortDirection = sortDirection,
        inboxScreenItemsResource = inboxScreenItemsResource
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun InboxScreen(
    modifier: Modifier = Modifier,
    handleScaffoldAction: (action: MainScaffoldStateAction) -> Unit,
    currentViewType: ViewTypeEnum,
    onAction: (action: InboxScreenUiAction) -> Unit,
    getIsTaskExpanded: (id: Long) -> Boolean,
    getIsSectionExpanded: (id: Long) -> Boolean,
    isLoading: Boolean,
    inboxScreenNavigationActions: InboxScreenNavigationActions,
    showCompleted: Boolean?,
    showArchived: Boolean?,
    inboxScreenItemsResource: AppResource<List<InboxScreenUiModel>>,
    sortDirection: SortDirection,
) {


    LaunchedEffect(Unit, currentViewType, showCompleted, showArchived, sortDirection) {
        handleScaffoldAction(
            MainScaffoldStateAction.OnSetTopAppBar(
                topAppBar = {
                    InboxTopAppBar(
                        onChangeViewType = { viewTypeEnum: ViewTypeEnum ->
                            onAction(
                                InboxScreenUiAction.OnChangeViewType(
                                    viewType = viewTypeEnum
                                )
                            )
                        },
                        currentViewType = currentViewType,
                        showCompleted = showCompleted,
                        showArchived = showArchived,
                        onSetShowCompleted = { showCompleted: Boolean? ->
                            onAction(
                                InboxScreenUiAction.OnSetShowCompleted(
                                    showCompleted = showCompleted
                                )
                            )
                        },
                        onSetShowArchived = { showArchived: Boolean? ->
                            onAction(
                                InboxScreenUiAction.OnSetShowArchived(
                                    showArchived = showArchived
                                )
                            )

                        },
                        sortDirection = sortDirection,
                        onSetSortDirection = { sortDirection ->
                            onAction(
                                InboxScreenUiAction
                                    .OnSetSortDirection(
                                        sortDirection = sortDirection
                                    )
                            )
                        }
                    )
                }
            )
        )
    }
    LaunchedEffect(Unit) {
        handleScaffoldAction(
            MainScaffoldStateAction.OnSetFloatingActionButton(
                floatingActionButton = {
                    AppFloatingActionMenuButton { onHide ->
                        InboxFloatingActionButtonMenuItems(
                            onHide = onHide,
                            onNavigateToAddTaskDialog = { args ->
                                inboxScreenNavigationActions.onNavigateToAddTaskDialog(args = args)
                            },
                            onNavigateToAddSectionDialog = {
                                inboxScreenNavigationActions.onNavigateToAddSectionDialog()
                            },
                        )
                    }
                }
            )
        )
    }
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        visible = inboxScreenItemsResource is AppResource.Loading || isLoading,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }

    when (val inboxScreenItems = inboxScreenItemsResource) {
        is AppResource.Error -> Unit
        is AppResource.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        AppResource.Stale -> Unit
        is AppResource.Success -> {
            Crossfade(
                targetState = currentViewType,
                label = "ViewTypeCrossFade",
                animationSpec = tween(),
            ) { viewType: ViewTypeEnum ->
                when (viewType) {
                    ViewTypeEnum.ListView -> {
                        ListViewContent(
                            modifier = modifier,
                            inboxScreenUiModels = inboxScreenItems.data,
                            onAction = onAction,
                            getIsTaskExpanded = getIsTaskExpanded,
                            getIsSectionExpanded = getIsSectionExpanded,
                            onToggleTask = { taskId: Long ->
                                onAction(
                                    InboxScreenUiAction.OnToggleTask(
                                        taskId = taskId,
                                    )
                                )
                            },
                            onToggleSection = { sectionId: Long ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnToggleSection(
                                            sectionId = sectionId
                                        )
                                )
                            },
                            onDeleteTask = { taskId: Long ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnDeleteTask(
                                            taskId = taskId,
                                        )
                                )
                            },
                            onDeleteSection = { sectionId: Long ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnDeleteSection(
                                            sectionId = sectionId
                                        )
                                )
                            },
                            onDuplicateTask = {
                                onAction(
                                    InboxScreenUiAction
                                        .OnDuplicateTask(
                                            taskId = it
                                        )
                                )
                            },
                            onToggleArchiveTask = { taskId: Long, title: String, isArchived: Boolean ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnToggleArchiveTask(
                                            taskId = taskId,
                                            title = title,
                                            isArchived = isArchived,
                                        )
                                )
                            },
                            onNavigateToTaskHashtags = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToTaskHashtagsDialog(
                                            args = args
                                        )
                                )
                            }
                        )
                    }

                    ViewTypeEnum.BoardView -> {
                        BoardViewContent(
                            modifier = modifier,
                            inboxScreenUiModels = inboxScreenItems.data,
                            onAction = onAction,
                            getIsTaskExpanded = getIsTaskExpanded,
                            getIsSectionExpanded = getIsSectionExpanded,
                            onToggleTask = { taskId: Long ->
                                onAction(
                                    InboxScreenUiAction.OnToggleTask(
                                        taskId = taskId,
                                    )
                                )
                            },
                            onToggleSection = { sectionId: Long ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnToggleSection(
                                            sectionId = sectionId
                                        )
                                )
                            },
                            onDeleteTask = { taskId: Long ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnDeleteTask(
                                            taskId = taskId,
                                        )
                                )
                            },
                            onDeleteSection = { sectionId: Long ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnDeleteSection(
                                            sectionId = sectionId
                                        )
                                )
                            },
                            onDuplicateTask = {
                                onAction(
                                    InboxScreenUiAction
                                        .OnDuplicateTask(
                                            taskId = it
                                        )
                                )
                            },
                            onToggleArchiveTask = { taskId: Long, title: String, isArchived: Boolean ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnToggleArchiveTask(
                                            taskId = taskId,
                                            title = title,
                                            isArchived = isArchived,
                                        )
                                )
                            },
                            onNavigateToTaskHashtags = { args ->
                                onAction(
                                    InboxScreenUiAction
                                        .OnNavigateToTaskHashtagsDialog(
                                            args = args
                                        )
                                )
                            }
                        )
                    }
                }

            }
        }
    }


}