package at.robthered.plan_me.features.move_task_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.MoveTaskDialogArgs
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items.LoadMoveTaskItemsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskUseCase
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.model.toMoveTaskUseCaseArgs
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.MoveTaskDialogNavigationEvent
import at.robthered.plan_me.features.move_task_dialog.presentation.navigation.MoveTaskDialogNavigationEventDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MoveTaskDialogViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val moveTaskDialogNavigationEventDispatcher: MoveTaskDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val moveTaskUseCase: MoveTaskUseCase,
    private val useCaseOperator: UseCaseOperator,
    loadMoveTaskItemsUseCase: LoadMoveTaskItemsUseCase,
) : ViewModel() {

    val appNavigationEvent = moveTaskDialogNavigationEventDispatcher
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


    private val _moveTaskDialogArgs = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_HANDLE_MOVE_TASK_DIALOG_ARGS,
            initialValue = MoveTaskDialogArgs(
                taskId = savedStateHandle.toRoute<Route.MoveTaskDialog>().taskId,
                parentTaskId = savedStateHandle.toRoute<Route.MoveTaskDialog>().parentTaskId,
                sectionId = savedStateHandle.toRoute<Route.MoveTaskDialog>().sectionId
            )
        )

    val moveTaskDialogArgs = _moveTaskDialogArgs.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val moveTaskItems: StateFlow<List<MoveTaskModel>> = loadMoveTaskItemsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = savedStateHandle[SAVED_STATE_HANDLE_MOVE_TASK_MOVE_ITEMS] ?: emptyList()
        ).also { flow ->
            viewModelScope
                .launch {
                    flow.collectLatest {
                        savedStateHandle[SAVED_STATE_HANDLE_MOVE_TASK_MOVE_ITEMS] = it
                    }
                }
        }

    fun onAction(action: MoveTaskDialogUiAction) {
        when (action) {
            MoveTaskDialogUiAction.OnNavigateBack -> onNavigateBack()
            is MoveTaskDialogUiAction.OnPickMoveTaskItem -> onPickMoveTaskItem(action)
        }
    }

    private fun onPickMoveTaskItem(action: MoveTaskDialogUiAction.OnPickMoveTaskItem) {

        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = MoveTaskLoadingStatus.MOVING_TASK.toUiText(),
                successMessageProvider = { MoveTaskSuccessMessage.TASK_MOVED.toUiText() },
                onSuccessAction = {
                    moveTaskDialogNavigationEventDispatcher.dispatch(
                        MoveTaskDialogNavigationEvent.OnNavigateBack
                    )
                },
            ) {
                moveTaskUseCase(
                    moveTaskUseCaseArgs = action.moveTaskModel.toMoveTaskUseCaseArgs(
                        taskId = _moveTaskDialogArgs.value.taskId
                    )
                )
            }
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            moveTaskDialogNavigationEventDispatcher
                .dispatch(
                    event = MoveTaskDialogNavigationEvent
                        .OnNavigateBack
                )
        }
    }


    companion object {
        const val SAVED_STATE_HANDLE_MOVE_TASK_DIALOG_ARGS =
            "SAVED_STATE_HANDLE_MOVE_TASK_DIALOG_ARGS"
        const val SAVED_STATE_HANDLE_MOVE_TASK_MOVE_ITEMS =
            "SAVED_STATE_HANDLE_MOVE_TASK_MOVE_ITEMS"
    }

}