package at.robthered.plan_me.features.task_statistics_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task.DeleteTaskUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history.DeleteTaskArchivedHistoryLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history.DeleteTaskArchivedHistorySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history.DeleteTaskArchivedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistoryLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistorySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistoryLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistorySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistoryLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistorySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_schedule_event.DeleteTaskScheduleEventUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history.DeleteTaskTitleHistoryLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history.DeleteTaskTitleHistorySuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history.DeleteTaskTitleHistoryUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics.LoadTaskStatisticsUseCase
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.TaskStatisticsDialogNavigationEvent
import at.robthered.plan_me.features.task_statistics_dialog.presentation.navigation.TaskStatisticsDialogNavigationEventDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TaskStatisticsDialogViewModel(
    savedStateHandle: SavedStateHandle,
    loadTaskStatisticsUseCase: LoadTaskStatisticsUseCase,
    private val deleteTaskTitleHistoryUseCase: DeleteTaskTitleHistoryUseCase,
    private val deleteTaskDescriptionHistoryUseCase: DeleteTaskDescriptionHistoryUseCase,
    private val deleteTaskPriorityHistoryUseCase: DeleteTaskPriorityHistoryUseCase,
    private val deleteTaskCompletedHistoryUseCase: DeleteTaskCompletedHistoryUseCase,
    private val deleteTaskArchivedHistoryUseCase: DeleteTaskArchivedHistoryUseCase,
    private val deleteTaskScheduleEventUseCase: DeleteTaskScheduleEventUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val useCaseOperator: UseCaseOperator,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val taskStatisticsDialogNavigationEventDispatcher: TaskStatisticsDialogNavigationEventDispatcher,
) : ViewModel() {

    val appNavigationEvent = taskStatisticsDialogNavigationEventDispatcher
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

    private val taskId = savedStateHandle.toRoute<Route.TaskStatisticsDialog>().taskId

    val taskStatisticsResource: StateFlow<AppResource<List<TaskStatisticsModel>>> =
        loadTaskStatisticsUseCase(taskId = taskId)
            .onStart {
                emit(AppResource.Loading())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppResource.Stale
            )


    fun onAction(action: TaskStatisticsDialogUiAction) {
        when (action) {
            is TaskStatisticsDialogUiAction.OnDeleteTaskArchivedHistory -> onDeleteTaskArchivedHistory(
                action
            )

            is TaskStatisticsDialogUiAction.OnDeleteTaskCompletedHistory -> onDeleteTaskCompletedHistory(
                action
            )

            is TaskStatisticsDialogUiAction.OnDeleteTaskDescriptionHistory -> onDeleteTaskDescriptionHistory(
                action
            )

            is TaskStatisticsDialogUiAction.OnDeleteTaskPriorityHistory -> onDeleteTaskPriorityHistory(
                action
            )

            is TaskStatisticsDialogUiAction.OnDeleteTaskTitleHistory -> onDeleteTaskTitleHistory(
                action
            )

            is TaskStatisticsDialogUiAction.OnDeleteSubTask -> onDeleteSubTask(action)
            TaskStatisticsDialogUiAction.OnNavigateBack -> onNavigateBack()
            is TaskStatisticsDialogUiAction.OnNavigateToTaskDetailsDialog -> onNavigateToTaskDetailsDialog(
                action
            )

            is TaskStatisticsDialogUiAction.OnNavigateToHashtagTasksDialog -> onNavigateToHashtagTasksDialog(
                action
            )

            is TaskStatisticsDialogUiAction.OnDeleteTaskScheduleEvent -> onDeleteTaskScheduleEvent(
                action
            )
        }
    }

    private fun onNavigateToHashtagTasksDialog(action: TaskStatisticsDialogUiAction.OnNavigateToHashtagTasksDialog) {
        viewModelScope.launch {
            taskStatisticsDialogNavigationEventDispatcher
                .dispatch(
                    TaskStatisticsDialogNavigationEvent.OnNavigateToHashtagTasksDialog(
                        args = action.args
                    )
                )
        }
    }

    private fun onNavigateToTaskDetailsDialog(action: TaskStatisticsDialogUiAction.OnNavigateToTaskDetailsDialog) {
        viewModelScope.launch {
            taskStatisticsDialogNavigationEventDispatcher
                .dispatch(
                    TaskStatisticsDialogNavigationEvent.OnNavigateToTaskDetailsDialog(
                        args = action.args
                    )
                )
        }
    }

    private fun onDeleteTaskScheduleEvent(action: TaskStatisticsDialogUiAction.OnDeleteTaskScheduleEvent) {
        viewModelScope
            .launch {
                useCaseOperator(
                    loadingStatus = DeleteTaskScheduleEventLoadingStatus.STARTING.toUiText(),
                    successMessageProvider = { DeleteTaskScheduleEventSuccessMessage.TASK_SCHEDULE_EVENT_DELETED_SUCCESS.toUiText() }
                ) {
                    deleteTaskScheduleEventUseCase(taskScheduleEventId = action.taskScheduleEventId)
                }
            }
    }

    private fun onDeleteSubTask(action: TaskStatisticsDialogUiAction.OnDeleteSubTask) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteTaskSuccessMessage.TASK_DELETED_SUCCESS.toUiText() }
            ) {
                deleteTaskUseCase(taskId = action.subTaskId)
            }
        }
    }

    private fun onDeleteTaskArchivedHistory(action: TaskStatisticsDialogUiAction.OnDeleteTaskArchivedHistory) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskArchivedHistoryLoadingStatus.LOADING_DELETE_TASK_ARCHIVED_HISTORY.toUiText(),
                successMessageProvider = { DeleteTaskArchivedHistorySuccessMessage.DELETE_TASK_ARCHIVED_HISTORY_COMPLETED.toUiText() }
            ) {
                deleteTaskArchivedHistoryUseCase(taskArchivedHistoryId = action.taskArchivedHistoryId)
            }
        }
    }

    private fun onDeleteTaskCompletedHistory(action: TaskStatisticsDialogUiAction.OnDeleteTaskCompletedHistory) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskCompletedHistoryLoadingStatus.LOADING_DELETE_TASK_COMPLETED_HISTORY.toUiText(),
                successMessageProvider = { DeleteTaskCompletedHistorySuccessMessage.DELETE_TASK_COMPLETED_HISTORY_COMPLETED.toUiText() }
            ) {
                deleteTaskCompletedHistoryUseCase(taskCompletedHistoryId = action.taskCompletedHistoryId)
            }
        }
    }

    private fun onDeleteTaskDescriptionHistory(action: TaskStatisticsDialogUiAction.OnDeleteTaskDescriptionHistory) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskDescriptionHistoryLoadingStatus.LOADING_DELETE_TASK_DESCRIPTION_HISTORY.toUiText(),
                successMessageProvider = { DeleteTaskDescriptionHistorySuccessMessage.DELETE_TASK_DESCRIPTION_HISTORY_COMPLETED.toUiText() }
            ) {
                deleteTaskDescriptionHistoryUseCase(taskDescriptionHistoryId = action.taskDescriptionHistoryId)
            }
        }
    }

    private fun onDeleteTaskPriorityHistory(action: TaskStatisticsDialogUiAction.OnDeleteTaskPriorityHistory) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskPriorityHistoryLoadingStatus.LOADING_DELETE_TASK_PRIORITY_HISTORY.toUiText(),
                successMessageProvider = { DeleteTaskPriorityHistorySuccessMessage.DELETE_TASK_PRIORITY_HISTORY_COMPLETED.toUiText() }
            ) {
                deleteTaskPriorityHistoryUseCase(taskPriorityHistoryId = action.taskPriorityHistoryId)
            }
        }
    }

    private fun onDeleteTaskTitleHistory(action: TaskStatisticsDialogUiAction.OnDeleteTaskTitleHistory) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskTitleHistoryLoadingStatus.LOADING_DELETE_TASK_TITLE_HISTORY.toUiText(),
                successMessageProvider = { DeleteTaskTitleHistorySuccessMessage.DELETE_TASK_TITLE_HISTORY_COMPLETED.toUiText() }
            ) {
                deleteTaskTitleHistoryUseCase(taskTitleHistoryId = action.taskTitleHistoryId)
            }
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            taskStatisticsDialogNavigationEventDispatcher.dispatch(
                TaskStatisticsDialogNavigationEvent.OnNavigateBack
            )
        }
    }

}