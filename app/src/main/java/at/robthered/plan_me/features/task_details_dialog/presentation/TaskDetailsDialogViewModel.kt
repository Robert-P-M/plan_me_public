package at.robthered.plan_me.features.task_details_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.event_bus.clearEvent
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.add_task_schedule_event.AddTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPrioritySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task.GetTaskDetailsDialogTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots.LoadTaskRootsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.TaskDetailsDialogNavigationEvent
import at.robthered.plan_me.features.task_details_dialog.presentation.navigation.TaskDetailsDialogNavigationEventDispatcher
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEvent
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskDetailsDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val loadTaskRootUseCase: LoadTaskRootsUseCase,
    private val toggleCompleteTaskUseCase: ToggleCompleteTaskUseCase,
    private val getTaskDetailsDialogTaskUseCase: GetTaskDetailsDialogTaskUseCase,
    private val useCaseOperator: UseCaseOperator,
    private val toggleArchiveTaskUseCase: ToggleArchiveTaskUseCase,
    private val priorityPickerEventBus: PriorityPickerEventBus,
    private val taskScheduleEventBus: TaskScheduleEventBus,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val deleteTaskHashtagReferenceUseCase: DeleteTaskHashtagReferenceUseCase,
    private val changeTaskPriorityUseCase: ChangeTaskPriorityUseCase,
    private val taskDetailsDialogNavigationEventDispatcher: TaskDetailsDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val addTaskScheduleEventUseCase: AddTaskScheduleEventUseCase,
) : ViewModel() {

    private val taskDetailsDialogArgs = savedStateHandle.toRoute<Route.TaskDetailsDialog>()


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

    val appNavigationEvent = taskDetailsDialogNavigationEventDispatcher
        .navigationEvents
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 0
        )


    val appUiEvent = appUiEventDispatcher
        .appUiEvent
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

    private val _taskId =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_TASK_DETAILS_DIALOG_TASK_ID,
                initialValue = taskDetailsDialogArgs.taskId
            )

    private data class TaskDetailsTrigger(
        val taskId: Long,
        val showCompleted: Boolean?,
        val showArchived: Boolean?,
        val sortDirection: SortDirection,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskDetailsResource: StateFlow<AppResource<TaskWithHashtagsAndChildrenModel>> =
        combine(
            _taskId,
            _showCompleted,
            _showArchived,
            _sortDirection,
        ) { id, completed, archived, direction ->
            TaskDetailsTrigger(
                taskId = id,
                showCompleted = completed,
                showArchived = archived,
                sortDirection = direction
            )
        }.flatMapLatest { trigger ->
            getTaskDetailsDialogTaskUseCase(
                taskId = trigger.taskId,
                depth = 5,
                showCompleted = trigger.showCompleted,
                showArchived = trigger.showArchived,
                sortDirection = trigger.sortDirection,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppResource.Stale
        )

    val taskDetails: StateFlow<TaskWithHashtagsAndChildrenModel?> = taskDetailsResource
        .filterIsInstance<AppResource.Success<TaskWithHashtagsAndChildrenModel?>>()
        .map { successState ->
            successState.data
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskRoots = _taskId
        .flatMapLatest { id ->
            loadTaskRootUseCase(taskId = id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )


    fun onAction(action: TaskDetailsDialogUiAction) {
        when (action) {
            TaskDetailsDialogUiAction.OnNavigateBack -> onNavigateBack()
            is TaskDetailsDialogUiAction.OnNavigateToSectionDetailsDialog -> onNavigateToSectionDetailsDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToTaskDetailsDialog -> onNavigateToTaskDetailsDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToAddTaskDialog -> onNavigateToAddTaskDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnToggleCompleteTask -> onToggleCompleteTask(action)
            is TaskDetailsDialogUiAction.OnNavigateToPriorityPickerDialog -> onNavigateToPriorityPickerDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnDeleteTask -> onDeleteTask(action)
            is TaskDetailsDialogUiAction.OnNavigateToUpdateTaskDialog -> onNavigateToUpdateTaskDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToUpdateHashtagNameDialog -> onNavigateToUpdateHashtagNameDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToTaskStatisticsDialog -> onNavigateToTaskStatisticsDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnToggleArchiveTask -> onToggleArchiveTask(action)
            is TaskDetailsDialogUiAction.OnDeleteTaskHashtagReference -> onDeleteTaskHashtagReference(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToTaskHashtagsDialog -> onNavigateToTaskHashtagsDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToHashtagTasksDialog -> onNavigateToHashtagTasksDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToMoveTaskDialog -> onNavigateToMoveTaskDialog(
                action
            )

            is TaskDetailsDialogUiAction.OnNavigateToTaskScheduleEventPickerDialog -> onNavigateToTaskScheduleEventPickerDialog(
                action
            )
        }
    }

    private fun onNavigateToTaskScheduleEventPickerDialog(action: TaskDetailsDialogUiAction.OnNavigateToTaskScheduleEventPickerDialog) {
        viewModelScope
            .launch {
                taskScheduleEventBus
                    .publish(
                        event = TaskScheduleEvent
                            .CurrentTaskScheduleEventModel(
                                taskScheduleEventModel = taskDetails.value?.taskWithUiHashtagsModel?.task?.taskSchedule
                            )
                    )
                taskScheduleEventBus
                    .publish(
                        event = TaskScheduleEvent
                            .CurrentAddTaskSchedule(
                                addTaskScheduleEventModel = taskDetails.value?.taskWithUiHashtagsModel?.task?.taskSchedule?.let {
                                    AddTaskScheduleEventModel(
                                        taskId = it.taskId,
                                        startDateInEpochDays = it.startDateInEpochDays,
                                        timeOfDayInMinutes = it.timeOfDayInMinutes,
                                        isNotificationEnabled = it.isNotificationEnabled,
                                        durationInMinutes = it.durationInMinutes,
                                        isFullDay = it.isFullDay
                                    )
                                } ?: AddTaskScheduleEventModel()
                            )
                    )
            }
        viewModelScope
            .launch {
                taskDetailsDialogNavigationEventDispatcher
                    .dispatch(
                        event = TaskDetailsDialogNavigationEvent
                            .OnNavigateToTaskScheduleEventDialog(
                                args = action.args
                            )
                    )
            }
    }

    private val observeCurrentAddTaskSchedule = taskScheduleEventBus
        .subscribeOn<TaskScheduleEvent, TaskScheduleEvent.NewAddTaskSchedule>(
            scope = viewModelScope,
        ) { event ->

            useCaseOperator(
                loadingStatus = AddTaskScheduleEventLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { AddTaskScheduleEventSuccessMessage.TASK_EVENT_CHANGED.toUiText() },
                onSuccessAction = {
                    onClearTaskScheduleEventBus()
                }
            ) {
                addTaskScheduleEventUseCase(
                    taskId = taskDetailsDialogArgs.taskId,
                    addTaskScheduleEventModel = event.addTaskScheduleEventModel
                )

            }
        }


    private fun onClearTaskScheduleEventBus() {
        viewModelScope
            .launch {
                taskScheduleEventBus.clearEvent(defaultEvent = TaskScheduleEvent.ClearEvent)
            }
    }

    private fun onNavigateToMoveTaskDialog(action: TaskDetailsDialogUiAction.OnNavigateToMoveTaskDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent
                .OnNavigateToMoveTaskDialog(
                    args = action.args
                )
        )
    }

    private fun onNavigate(event: TaskDetailsDialogNavigationEvent) {
        viewModelScope
            .launch {
                taskDetailsDialogNavigationEventDispatcher
                    .dispatch(
                        event = event
                    )
            }
    }

    private fun onNavigateToHashtagTasksDialog(action: TaskDetailsDialogUiAction.OnNavigateToHashtagTasksDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent
                .OnNavigateToHashtagTasksDialog(
                    args = action.args
                )
        )
    }

    private fun onNavigateToTaskHashtagsDialog(action: TaskDetailsDialogUiAction.OnNavigateToTaskHashtagsDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent
                .OnNavigateToTaskHashtagsDialog(
                    args = action.args
                )
        )

    }

    private fun onDeleteTaskHashtagReference(action: TaskDetailsDialogUiAction.OnDeleteTaskHashtagReference) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskHashtagReferenceLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteTaskHashtagReferenceSuccessMessage.TASK_HASHTAG_REFERENCE_SUCCESS.toUiText() },
            ) {
                deleteTaskHashtagReferenceUseCase(
                    taskId = action.taskId,
                    hashtagId = action.hashtagId
                )
            }
        }
    }

    private fun onNavigateToUpdateHashtagNameDialog(action: TaskDetailsDialogUiAction.OnNavigateToUpdateHashtagNameDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent.OnNavigateToUpdateHashtagNameDialog(
                args = action.args
            )
        )
    }

    private fun onNavigateToUpdateTaskDialog(action: TaskDetailsDialogUiAction.OnNavigateToUpdateTaskDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent.OnNavigateToUpdateTaskDialog(
                args = action.args
            )
        )
    }

    private fun onNavigateToTaskStatisticsDialog(action: TaskDetailsDialogUiAction.OnNavigateToTaskStatisticsDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent
                .OnNavigateToTaskStatisticsDialog(
                    args = action.args
                )
        )
    }


    private fun onNavigateToAddTaskDialog(action: TaskDetailsDialogUiAction.OnNavigateToAddTaskDialog) {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent.OnNavigateToAddTaskDialog(
                args = action.args
            )
        )
    }

    private fun onNavigateBack() {
        onNavigate(
            event = TaskDetailsDialogNavigationEvent.OnNavigateBack
        )
    }

    private fun onDeleteTask(action: TaskDetailsDialogUiAction.OnDeleteTask) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteTaskSuccessMessage.TASK_DELETED_SUCCESS.toUiText() },
                onSuccessAction = {
                    taskDetailsDialogNavigationEventDispatcher.dispatch(
                        event = TaskDetailsDialogNavigationEvent.OnNavigateBack
                    )
                }
            ) {
                deleteTaskUseCase(taskId = action.taskId)
            }
        }
    }

    private fun onToggleCompleteTask(action: TaskDetailsDialogUiAction.OnToggleCompleteTask) {
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
                },
            ) {
                toggleCompleteTaskUseCase(taskId = action.taskId)
            }
        }
    }


    private fun onToggleArchiveTask(
        action: TaskDetailsDialogUiAction.OnToggleArchiveTask,
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

    private fun onNavigateToTaskDetailsDialog(action: TaskDetailsDialogUiAction.OnNavigateToTaskDetailsDialog) {
        viewModelScope.launch {
            taskDetailsDialogNavigationEventDispatcher.dispatch(
                event = TaskDetailsDialogNavigationEvent.OnNavigateToTaskDetailsDialogDialog(taskId = action.taskId)
            )
        }
    }

    private fun onNavigateToSectionDetailsDialog(action: TaskDetailsDialogUiAction.OnNavigateToSectionDetailsDialog) {
        viewModelScope.launch {
            taskDetailsDialogNavigationEventDispatcher.dispatch(
                event = TaskDetailsDialogNavigationEvent.OnNavigateToSectionDetailsDialog(sectionId = action.sectionId)
            )
        }
    }

    private val observeNewTaskPriority = priorityPickerEventBus
        .subscribeOn<PriorityPickerEvent, PriorityPickerEvent.NewTaskPriority>(
            scope = viewModelScope
        ) { event ->
            useCaseOperator(
                loadingStatus = ChangeTaskPriorityLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { ChangeTaskPrioritySuccessMessage.TASK_PRIORITY_CHANGED.toUiText() },
                onSuccessAction = {
                    priorityPickerEventBus.clearEvent(PriorityPickerEvent.ClearEvent)
                }
            ) {

                changeTaskPriorityUseCase(
                    priorityEnum = event.priorityEnum,
                    taskId = event.taskId
                )

            }
        }

    private fun onNavigateToPriorityPickerDialog(action: TaskDetailsDialogUiAction.OnNavigateToPriorityPickerDialog) {
        viewModelScope.launch {
            priorityPickerEventBus
                .publish(
                    PriorityPickerEvent
                        .CurrentTaskPriority(
                            priorityEnum = action.priorityEnum,
                            taskId = action.taskId,
                        )
                )
            onNavigate(
                event = TaskDetailsDialogNavigationEvent.OnNavigateToPriorityPickerDialog
            )


        }
    }


    companion object {
        const val SAVED_STATE_TASK_DETAILS_DIALOG_TASK_ID =
            "SAVED_STATE_TASK_DETAILS_DIALOG_TASK_ID"
        const val SAVED_STATE_SHOW_ARCHIVED_TASKS = "SAVED_STATE_SHOW_ARCHIVED_TASKS"
        const val SAVED_STATE_SHOW_COMPLETED_TASKS = "SAVED_STATE_SHOW_COMPLETED_TASKS"
        const val SAVED_STATE_SORT_DIRECTION = "SAVED_STATE_SORT_DIRECTION"
    }

}