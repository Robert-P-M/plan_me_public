package at.robthered.plan_me.features.hashtag_tasks_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.common.domain.HashtagTasksDialogAppResourceError
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks.LoadHashtagWithTasksUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskUseCase
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.HashtagTasksDialogNavigationEvent
import at.robthered.plan_me.features.hashtag_tasks_dialog.presentation.navigation.HashtagTasksDialogNavigationEventDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HashtagTasksDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val hashtagTasksDialogNavigationEventDispatcher: HashtagTasksDialogNavigationEventDispatcher,
    private val deleteTaskHashtagReferenceUseCase: DeleteTaskHashtagReferenceUseCase,
    private val loadHashtagWithTasksUseCase: LoadHashtagWithTasksUseCase,
    private val toggleCompleteTaskUseCase: ToggleCompleteTaskUseCase,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val useCaseOperator: UseCaseOperator,
) : ViewModel() {

    private val hashtagTasksDialogArgs = savedStateHandle.toRoute<Route.HashtagTasksDialog>()

    val appNavigationEvent = hashtagTasksDialogNavigationEventDispatcher
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

    private val _hashtagId =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_HASHTAG_ID,
                initialValue = hashtagTasksDialogArgs.hashtagId
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val hashtagTasksResource: StateFlow<AppResource<HashtagWithTasksModel>> = _hashtagId
        .flatMapLatest { id ->
            loadHashtagWithTasksUseCase(hashtagId = id)
                .map { result ->
                    result?.let { AppResource.Success(it) }
                        ?: AppResource.Error(HashtagTasksDialogAppResourceError.HASHTAG_NOT_FOUND)
                }
                .onStart { emit(AppResource.Loading()) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppResource.Stale
        )


    fun onAction(action: HashtagTasksDialogUiActions) {
        when (action) {
            is HashtagTasksDialogUiActions.OnDeleteTaskHashtagReference -> onDeleteTaskHashtagReference(
                action
            )

            is HashtagTasksDialogUiActions.OnNavigateToTaskDetailsDialog -> onNavigateToTaskDetailsDialog(
                action
            )

            is HashtagTasksDialogUiActions.OnNavigateBack -> onNavigateBack()
            is HashtagTasksDialogUiActions.OnToggleCompleteTask -> onToggleCompleteTask(action)
            is HashtagTasksDialogUiActions.OnNavigateToUpdateHashtagNameDialog -> onNavigateToUpdateHashtagNameDialog(
                action
            )

            is HashtagTasksDialogUiActions.OnNavigateToUpdateTaskDialog -> onNavigateToUpdateTaskDialog(
                action
            )

        }
    }

    fun onNavigateToUpdateTaskDialog(action: HashtagTasksDialogUiActions.OnNavigateToUpdateTaskDialog) {
        viewModelScope
            .launch {
                hashtagTasksDialogNavigationEventDispatcher
                    .dispatch(
                        event = HashtagTasksDialogNavigationEvent
                            .OnNavigateToUpdateTaskDialog(
                                args = action.args
                            )
                    )
            }
    }

    private fun onToggleCompleteTask(action: HashtagTasksDialogUiActions.OnToggleCompleteTask) {
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
            ) { toggleCompleteTaskUseCase(taskId = action.taskId) }
        }
    }

    private fun onNavigateToUpdateHashtagNameDialog(action: HashtagTasksDialogUiActions.OnNavigateToUpdateHashtagNameDialog) {
        viewModelScope
            .launch {
                hashtagTasksDialogNavigationEventDispatcher
                    .dispatch(
                        event = HashtagTasksDialogNavigationEvent
                            .OnNavigateToUpdateHashtagNameDialog(
                                args = action.args
                            )
                    )
            }
    }

    private fun onNavigateToTaskDetailsDialog(action: HashtagTasksDialogUiActions.OnNavigateToTaskDetailsDialog) {
        viewModelScope
            .launch {
                hashtagTasksDialogNavigationEventDispatcher
                    .dispatch(
                        event = HashtagTasksDialogNavigationEvent
                            .OnNavigateToTaskDetailsDialog(
                                args = action.args
                            )
                    )
            }
    }

    private fun onNavigateBack() {
        viewModelScope
            .launch {
                hashtagTasksDialogNavigationEventDispatcher
                    .dispatch(
                        event = HashtagTasksDialogNavigationEvent
                            .OnNavigateBack
                    )
            }
    }

    private fun onDeleteTaskHashtagReference(action: HashtagTasksDialogUiActions.OnDeleteTaskHashtagReference) {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = DeleteTaskHashtagReferenceLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { DeleteTaskHashtagReferenceSuccessMessage.TASK_HASHTAG_REFERENCE_SUCCESS.toUiText() },
            ) {
                deleteTaskHashtagReferenceUseCase(
                    taskId = action.taskId,
                    hashtagId = _hashtagId.value
                )
            }
        }
    }

    companion object {
        const val SAVED_STATE_HANDLE_HASHTAG_ID = "SAVED_STATE_HANDLE_HASHTAG_ID"
    }

}