package at.robthered.plan_me.features.update_task_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.event_bus.clearEvent
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModelError
import at.robthered.plan_me.features.data_source.domain.use_case.load_update_task_model.LoadUpdateTaskModelUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskUseCase
import at.robthered.plan_me.features.data_source.domain.validation.update_task.UpdateTaskModelValidator
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.update_task_dialog.presentation.helper.CanSaveUpdateTaskChecker
import at.robthered.plan_me.features.update_task_dialog.presentation.helper.UpdateTaskModelChangeChecker
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.UpdateTaskDialogNavigationEvent
import at.robthered.plan_me.features.update_task_dialog.presentation.navigation.UpdateTaskDialogNavigationEventDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UpdateTaskDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val loadUpdateTaskModelUseCase: LoadUpdateTaskModelUseCase,
    private val updateTaskModelChangeChecker: UpdateTaskModelChangeChecker,
    private val canSaveUpdateTaskChecker: CanSaveUpdateTaskChecker,
    private val updateTaskModelValidator: UpdateTaskModelValidator,
    private val priorityPickerEventBus: PriorityPickerEventBus,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val useCaseOperator: UseCaseOperator,
    private val updateTaskDialogNavigationEventDispatcher: UpdateTaskDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
) : ViewModel() {

    private val taskIdFromArgs = savedStateHandle.toRoute<Route.UpdateTaskDialog>().taskId

    val appNavigationEvent = updateTaskDialogNavigationEventDispatcher
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


    private val taskId: StateFlow<Long> = savedStateHandle
        .getStateFlow(
            key = SAVED_STATE_TASK_ID,
            initialValue = taskIdFromArgs
        )

    private val _initialUpdateTaskModel: MutableStateFlow<UpdateTaskModel> =
        MutableStateFlow(UpdateTaskModel())

    private val _updateTaskModel: MutableStateFlow<UpdateTaskModel> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_UPDATE_TASK_MODEL,
                initialValue = UpdateTaskModel()
            )

    val updateTaskModel: StateFlow<UpdateTaskModel> = _updateTaskModel.asStateFlow()

    private val _updateTaskModelError: MutableStateFlow<UpdateTaskModelError> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_UPDATE_TASK_MODEL_ERROR,
                initialValue = UpdateTaskModelError()
            )

    val updateTaskModelError: StateFlow<UpdateTaskModelError> = _updateTaskModelError.asStateFlow()

    private val observeUpdateTaskModelForError = _updateTaskModel
        .onEach { currentModel ->
            _updateTaskModelError.update {
                updateTaskModelValidator(currentModel)
            }
        }
        .launchIn(viewModelScope)

    val canSave = updateTaskModelError
        .map {
            canSaveUpdateTaskChecker(updateTaskModelError = it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val observeTaskId = taskId
        .onEach { id ->
            val model = loadUpdateTaskModelUseCase(taskId = id).first()
            _initialUpdateTaskModel.update {
                model
            }
            _updateTaskModel.update {
                model
            }
        }
        .launchIn(viewModelScope)

    val didModelChange = updateTaskModel
        .map { currentModel ->
            updateTaskModelChangeChecker(
                initialModel = _initialUpdateTaskModel.value,
                currentModel = currentModel
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    fun onAction(action: UpdateTaskDialogUiAction) {

        when (action) {
            UpdateTaskDialogUiAction.OnNavigateBack -> onNavigateBack()
            UpdateTaskDialogUiAction.OnNavigateToPriorityPickerDialog -> onNavigateToPriorityPickerDialog()
            UpdateTaskDialogUiAction.OnResetState -> onResetState()
            UpdateTaskDialogUiAction.OnUpdateTask -> onUpdateTask()
            is UpdateTaskDialogUiAction.OnChangeDescription -> onChangeDescription(action)
            is UpdateTaskDialogUiAction.OnChangePriority -> onChangePriority(action)
            is UpdateTaskDialogUiAction.OnChangeTitle -> onChangeTitle(action)
        }
    }

    private fun onChangeTitle(action: UpdateTaskDialogUiAction.OnChangeTitle) {
        _updateTaskModel.update {
            it.copy(
                title = action.title
            )
        }
    }

    private fun onChangeDescription(action: UpdateTaskDialogUiAction.OnChangeDescription) {
        _updateTaskModel
            .update {
                it.copy(
                    description = action.description
                )
            }
    }

    private fun onChangePriority(action: UpdateTaskDialogUiAction.OnChangePriority) {
        _updateTaskModel
            .update {
                it.copy(
                    priorityEnum = action.priority
                )
            }
    }

    val observe = priorityPickerEventBus
        .subscribeOn<PriorityPickerEvent, PriorityPickerEvent.NewPriority>(
            scope = viewModelScope
        ) { event ->
            onAction(
                UpdateTaskDialogUiAction
                    .OnChangePriority(
                        priority = event.priorityEnum
                    )
            )
            priorityPickerEventBus.clearEvent(PriorityPickerEvent.ClearEvent)
        }

    private fun onNavigateBack() {
        viewModelScope.launch {
            updateTaskDialogNavigationEventDispatcher.dispatch(UpdateTaskDialogNavigationEvent.OnNavigateBack)
        }
    }

    private fun onNavigateToPriorityPickerDialog() {
        viewModelScope.launch {
            priorityPickerEventBus
                .publish(
                    PriorityPickerEvent
                        .CurrentPriority(
                            priorityEnum = updateTaskModel.value.priorityEnum
                        )
                )
            updateTaskDialogNavigationEventDispatcher.dispatch(UpdateTaskDialogNavigationEvent.OnNavigateToPriorityPickerDialog)
        }
    }

    private fun onResetState() {
        _updateTaskModel.value = _initialUpdateTaskModel.value
    }

    private fun onUpdateTask() {
        viewModelScope.launch {

            useCaseOperator(
                loadingStatus = UpdateTaskLoadingStatus.STARTING.toUiText(),
                successMessageProvider = { UpdateTaskSuccessMessage.TASK_UPDATED.toUiText() },
                onSuccessAction = {
                    updateTaskDialogNavigationEventDispatcher.dispatch(
                        UpdateTaskDialogNavigationEvent.OnNavigateBack
                    )
                }
            ) {
                updateTaskUseCase(updateTaskModel = _updateTaskModel.value)

            }
        }
    }


    companion object {
        const val SAVED_STATE_TASK_ID = "SAVED_STATE_TASK_ID"
        const val SAVED_STATE_UPDATE_TASK_MODEL = "SAVED_STATE_UPDATE_TASK_MODEL"
        const val SAVED_STATE_UPDATE_TASK_MODEL_ERROR = "SAVED_STATE_UPDATE_TASK_MODEL_ERROR"
    }
}