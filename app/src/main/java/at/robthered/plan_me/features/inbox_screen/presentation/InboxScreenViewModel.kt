package at.robthered.plan_me.features.inbox_screen.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.utils.ext.toggle
import at.robthered.plan_me.features.data_source.domain.model.InboxScreenUiModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.view_type.ViewTypeEnum
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPrioritySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_items.GetInboxScreenItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.InboxScreenNavigationEvent
import at.robthered.plan_me.features.inbox_screen.presentation.navigation.InboxScreenNavigationEventDispatcher
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class InboxScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val toggleCompleteTaskUseCase: ToggleCompleteTaskUseCase,
    private val toggleArchiveTaskUseCase: ToggleArchiveTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val deleteSectionUseCase: DeleteSectionUseCase,
    private val priorityPickerEventBus: PriorityPickerEventBus,
    private val changeTaskPriorityUseCase: ChangeTaskPriorityUseCase,
    private val duplicateTaskUseCase: DuplicateTaskUseCase,
    private val getInboxScreenItemsUseCase: GetInboxScreenItemsUseCase,
    private val inboxScreenNavigationEventDispatcher: InboxScreenNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val useCaseOperator: UseCaseOperator,
) : ViewModel() {


    val appNavigationEvent = inboxScreenNavigationEventDispatcher
        .navigationEvents
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 0
        )

    val isLoading = appUiEventDispatcher
        .isLoading
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    private val _currentViewType =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_VIEW_TYPE,
                initialValue = ViewTypeEnum.BoardView
            )

    private val _showCompleted: MutableStateFlow<Boolean?> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_SHOW_COMPLETED_TASKS,
                initialValue = null
            )
    val showCompleted = _showCompleted.asStateFlow()

    private val _sortDirection: MutableStateFlow<SortDirection> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_SORT_DIRECTION,
                initialValue = SortDirection.DESC
            )

    val sortDirection = _sortDirection.asStateFlow()

    private val _showArchived: MutableStateFlow<Boolean?> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_SHOW_ARCHIVED_TASKS,
                initialValue = null
            )

    val showArchived = _showArchived.asStateFlow()

    val currentViewType: StateFlow<ViewTypeEnum> =
        _currentViewType.asStateFlow()

    private val _expandedTaskIds = savedStateHandle
        .getMutableStateFlow<Set<Long>>(
            key = SAVED_STATE_EXPANDED_TASKS,
            initialValue = emptySet()
        )

    val expandedTaskIds: StateFlow<Set<Long>> =
        _expandedTaskIds.asStateFlow()

    private val _expandedSectionIds =
        savedStateHandle
            .getMutableStateFlow<Set<Long>>(
                key = SAVED_STATE_EXPANDED_SECTIONS,
                initialValue = emptySet()
            )

    val expandedSectionIds: StateFlow<Set<Long>> =
        _expandedSectionIds.asStateFlow()


    fun onAction(action: InboxScreenUiAction) {
        when (action) {
            is InboxScreenUiAction.OnToggleSection ->
                onToggleSection(action)

            is InboxScreenUiAction.OnToggleTask ->
                onToggleTask(action)

            is InboxScreenUiAction.OnChangeViewType ->
                onChangeViewType(action)

            is InboxScreenUiAction.OnToggleCompleteTask -> onToggleCompleteTask(action)
            is InboxScreenUiAction.OnDeleteTask -> onDeleteTask(action)
            is InboxScreenUiAction.OnDeleteSection -> onDeleteSection(action)
            is InboxScreenUiAction.OnNavigateToPriorityPickerDialog -> onNavigateToPriorityPickerDialog(
                action
            )

            is InboxScreenUiAction.OnToggleArchiveTask -> onToggleArchiveTask(action)

            is InboxScreenUiAction.OnNavigateToTaskDetailsDialog -> onNavigateToTaskDetailsDialog(
                action
            )

            InboxScreenUiAction.OnNavigateToAddSectionDialog -> onNavigateToAddSectionDialog()
            is InboxScreenUiAction.OnNavigateToTaskStatisticsDialog -> onNavigateToTaskStatisticsDialog(
                action
            )

            is InboxScreenUiAction.OnDuplicateTask -> onDuplicateTask(action)
            is InboxScreenUiAction.OnNavigateToAddTaskDialog -> onNavigateToAddTaskDialog(action)
            is InboxScreenUiAction.OnNavigateToUpdateTaskDialog -> onNavigateToUpdateTaskDialog(
                action
            )

            is InboxScreenUiAction.OnNavigateToUpdateSectionTitleDialog -> onNavigateToUpdateSectionTitleDialog(
                action
            )

            is InboxScreenUiAction.OnSetShowArchived -> onSetShowArchived(action)
            is InboxScreenUiAction.OnSetShowCompleted -> onSetShowCompleted(action)
            is InboxScreenUiAction.OnNavigateToTaskHashtagsDialog -> onNavigateToTaskHashtagsDialog(
                action
            )

            is InboxScreenUiAction.OnNavigateToMoveTaskDialog -> onNavigateToMoveTaskDialog(action)
            is InboxScreenUiAction.OnSetSortDirection -> onSetSortDirection(action)
        }
    }

    private fun onNavigateToMoveTaskDialog(action: InboxScreenUiAction.OnNavigateToMoveTaskDialog) {
        viewModelScope
            .launch {
                inboxScreenNavigationEventDispatcher
                    .dispatch(
                        event = InboxScreenNavigationEvent.OnNavigateToMoveTaskDialog(
                            args = action.args
                        )
                    )
            }
    }

    private fun onSetSortDirection(action: InboxScreenUiAction.OnSetSortDirection) {
        _sortDirection
            .update { action.sortDirection }
    }

    private fun onNavigateToTaskHashtagsDialog(action: InboxScreenUiAction.OnNavigateToTaskHashtagsDialog) {
        viewModelScope
            .launch {
                inboxScreenNavigationEventDispatcher
                    .dispatch(
                        event = InboxScreenNavigationEvent.OnNavigateToTaskHashtagsDialog(
                            args = action.args
                        )
                    )
            }
    }

    private fun onSetShowArchived(action: InboxScreenUiAction.OnSetShowArchived) {
        _showArchived.update {
            action.showArchived
        }
    }

    private fun onSetShowCompleted(action: InboxScreenUiAction.OnSetShowCompleted) {
        _showCompleted.update {
            action.showCompleted
        }
    }

    private fun onNavigateToUpdateSectionTitleDialog(
        action: InboxScreenUiAction.OnNavigateToUpdateSectionTitleDialog,
    ) {
        viewModelScope
            .launch {
                inboxScreenNavigationEventDispatcher
                    .dispatch(
                        event = InboxScreenNavigationEvent.OnNavigateToUpdateSectionTitleDialog(
                            args = action.args
                        )
                    )
            }
    }

    private fun onNavigateToUpdateTaskDialog(
        action: InboxScreenUiAction.OnNavigateToUpdateTaskDialog,
    ) {
        viewModelScope
            .launch {
                inboxScreenNavigationEventDispatcher
                    .dispatch(
                        InboxScreenNavigationEvent.OnNavigateToUpdateTaskDialog(
                            args = action.args
                        )
                    )
            }
    }

    private fun onNavigateToAddTaskDialog(
        action: InboxScreenUiAction.OnNavigateToAddTaskDialog,
    ) {
        viewModelScope.launch {
            inboxScreenNavigationEventDispatcher
                .dispatch(
                    InboxScreenNavigationEvent.OnNavigateToAddTaskDialog(
                        args = action.args
                    )
                )
        }
    }


    private fun onDuplicateTask(
        action: InboxScreenUiAction.OnDuplicateTask,
    ) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DuplicateTaskLoadingStatus.DUPLICATING.toUiText(),
                successMessageProvider = { DuplicateTaskSuccessMessage.COMPLETED.toUiText() },
            ) {
                duplicateTaskUseCase(taskId = action.taskId)
            }
        }
    }


    private fun onNavigateToTaskStatisticsDialog(
        action: InboxScreenUiAction.OnNavigateToTaskStatisticsDialog,
    ) {
        viewModelScope.launch {
            inboxScreenNavigationEventDispatcher
                .dispatch(
                    event = InboxScreenNavigationEvent
                        .OnNavigateToTaskStatisticsDialog(
                            args = action.args
                        )
                )
        }
    }

    private val _observe = priorityPickerEventBus
        .subscribeOn<PriorityPickerEvent, PriorityPickerEvent.NewTaskPriority>(
            scope = viewModelScope
        ) { event ->
            useCaseOperator(
                loadingStatus = ChangeTaskPriorityLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { ChangeTaskPrioritySuccessMessage.TASK_PRIORITY_CHANGED.toUiText() },
            ) {
                changeTaskPriorityUseCase(
                    priorityEnum = event.priorityEnum,
                    taskId = event.taskId
                )
            }
        }


    private fun onNavigateToAddSectionDialog() {
        viewModelScope.launch {
            inboxScreenNavigationEventDispatcher
                .dispatch(
                    InboxScreenNavigationEvent
                        .OnNavigateToAddSectionDialog
                )
        }
    }

    private fun onNavigateToTaskDetailsDialog(
        action: InboxScreenUiAction.OnNavigateToTaskDetailsDialog,
    ) {
        viewModelScope.launch {
            inboxScreenNavigationEventDispatcher
                .dispatch(
                    InboxScreenNavigationEvent.OnNavigateToTaskDetailsDialog(
                        args = action.args
                    )
                )
        }
    }


    private fun onNavigateToPriorityPickerDialog(
        action: InboxScreenUiAction.OnNavigateToPriorityPickerDialog,
    ) {
        viewModelScope.launch {
            priorityPickerEventBus
                .publish(
                    PriorityPickerEvent
                        .CurrentTaskPriority(
                            priorityEnum = action.args.priorityEnum,
                            taskId = action.args.taskId,
                        )
                )
            inboxScreenNavigationEventDispatcher
                .dispatch(
                    InboxScreenNavigationEvent
                        .OnNavigateToPriorityPickerDialog
                )
        }
    }

    private fun onDeleteSection(
        action: InboxScreenUiAction.OnDeleteSection,
    ) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteSectionLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteSectionSuccessMessage.SECTION_DELETED_SUCCESS.toUiText() },
            ) {
                deleteSectionUseCase(sectionId = action.sectionId)
            }

        }
    }

    private fun onDeleteTask(
        action: InboxScreenUiAction.OnDeleteTask,
    ) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteTaskSuccessMessage.TASK_DELETED_SUCCESS.toUiText() }
            ) { deleteTaskUseCase(taskId = action.taskId) }
        }
    }

    private fun onToggleCompleteTask(
        action: InboxScreenUiAction.OnToggleCompleteTask,
    ) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = if (action.isCompleted)
                    ToggleCompleteTaskLoadingStatus.SWITCH_TO_UNCOMPLETED.toUiText(title = action.title)
                else
                    ToggleCompleteTaskLoadingStatus.SWITCH_TO_COMPLETED.toUiText(title = action.title),
                successMessageProvider = {
                    if (action.isCompleted)
                        ToggleCompleteTaskSuccessMessage.TASK_UN_COMPLETED.toUiText(title = action.title)
                    else
                        ToggleCompleteTaskSuccessMessage.TASK_COMPLETED.toUiText(title = action.title)
                }
            ) {
                toggleCompleteTaskUseCase(taskId = action.taskId)
            }
        }
    }

    private fun onToggleArchiveTask(
        action: InboxScreenUiAction.OnToggleArchiveTask,
    ) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = if (action.isArchived)
                    ToggleArchiveTaskLoadingStatus.SWITCH_TO_UN_ARCHIVED.toUiText(title = action.title)
                else
                    ToggleArchiveTaskLoadingStatus.SWITCH_TO_ARCHIVED.toUiText(title = action.title),
                successMessageProvider = {
                    if (action.isArchived)
                        ToggleArchiveTaskSuccessMessage.TASK_UN_ARCHIVED.toUiText(title = action.title)
                    else
                        ToggleArchiveTaskSuccessMessage.TASK_ARCHIVED.toUiText(title = action.title)
                },
            ) {
                toggleArchiveTaskUseCase(taskId = action.taskId)
            }
        }
    }

    private fun onToggleSection(
        action: InboxScreenUiAction.OnToggleSection,
    ) {
        _expandedSectionIds
            .update {
                it.toggle(id = action.sectionId)
            }
    }

    private fun onToggleTask(
        action: InboxScreenUiAction.OnToggleTask,
    ) {
        _expandedTaskIds
            .update {
                it.toggle(id = action.taskId)
            }
    }

    private fun onChangeViewType(
        action: InboxScreenUiAction.OnChangeViewType,
    ) {
        _currentViewType
            .update {
                action.viewType
            }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val inboxScreenItemsResource: StateFlow<AppResource<List<InboxScreenUiModel>>> = combine(
        _showArchived.onStart { emit(null) },
        _showCompleted.onStart { emit(null) },
        _sortDirection.onStart { emit(SortDirection.DESC) },
    ) { archived, completed, direction ->
        Triple(archived, completed, direction)
    }.flatMapLatest { (arch, comp, dir) ->
        getInboxScreenItemsUseCase(
            depth = 10,
            showCompleted = comp,
            showArchived = arch,
            sortDirection = dir
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppResource.Stale
    )


    companion object {
        const val SAVED_STATE_EXPANDED_TASKS = "SAVED_STATE_EXPANDED_TASKS"
        const val SAVED_STATE_EXPANDED_SECTIONS = "SAVED_STATE_EXPANDED_SECTIONS"
        const val SAVED_STATE_VIEW_TYPE = "SAVED_STATE_VIEW_TYPE"
        const val SAVED_STATE_SHOW_ARCHIVED_TASKS = "SAVED_STATE_SHOW_ARCHIVED_TASKS"
        const val SAVED_STATE_SHOW_COMPLETED_TASKS = "SAVED_STATE_SHOW_COMPLETED_TASKS"
        const val SAVED_STATE_SORT_DIRECTION = "SAVED_STATE_SORT_DIRECTION"
    }

}