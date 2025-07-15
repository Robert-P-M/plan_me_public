package at.robthered.plan_me.features.add_task_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import at.robthered.plan_me.features.add_task_dialog.presentation.helper.AddTaskModelChangeChecker
import at.robthered.plan_me.features.add_task_dialog.presentation.helper.CanSaveAddTaskChecker
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.AddTaskDialogNavigationEvent
import at.robthered.plan_me.features.add_task_dialog.presentation.navigation.AddTaskDialogNavigationEventDispatcher
import at.robthered.plan_me.features.common.domain.HashtagPickerEventBus
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.event_bus.clearEvent
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.common.presentation.helper.UseCaseOperator
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModelError
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskLoadingStatus
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskSuccessMessage
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskUseCase
import at.robthered.plan_me.features.data_source.domain.validation.add_task.AddTaskModelValidator
import at.robthered.plan_me.features.data_source.presentation.ext.loading_status.toUiText
import at.robthered.plan_me.features.data_source.presentation.ext.success_message.toUiText
import at.robthered.plan_me.features.hashtag_picker_dialog.presentation.hashtag_picker_event.HashtagPickerEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEvent
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTaskDialogViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val addTaskModelValidator: AddTaskModelValidator,
    private val addTaskModelChangeChecker: AddTaskModelChangeChecker,
    private val canSaveAddTaskChecker: CanSaveAddTaskChecker,
    private val addTaskUseCase: AddTaskUseCase,
    private val priorityPickerEventBus: PriorityPickerEventBus,
    private val hashtagPickerEventBus: HashtagPickerEventBus,
    private val taskScheduleEventBus: TaskScheduleEventBus,
    private val addTaskDialogNavigationEventDispatcher: AddTaskDialogNavigationEventDispatcher,
    appUiEventDispatcher: AppUiEventDispatcher,
    private val useCaseOperator: UseCaseOperator,
) : ViewModel() {

    private val route by lazy {
        savedStateHandle.toRoute<Route.AddTaskDialog>()
    }

    private val trueInitialDialogState by lazy {
        AddTaskModel()
            .copy(
                sectionId = route
                    .sectionId,
                parentTaskId = route
                    .parentTaskId,
            )
    }

    val appNavigationEvent = addTaskDialogNavigationEventDispatcher
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

    private val _addTaskModel = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_ADD_TASK_MODEL,
            initialValue = trueInitialDialogState
        )


    val addTaskModel: StateFlow<AddTaskModel> =
        _addTaskModel.asStateFlow()

    private val _observeNewAddTaskSchedule =
        taskScheduleEventBus
            .subscribeOn<TaskScheduleEvent, TaskScheduleEvent.NewAddTaskSchedule>(
                scope = viewModelScope
            ) { event ->
                onSetNewAddTaskSchedule(event)
            }


    private fun onSetNewAddTaskSchedule(event: TaskScheduleEvent.NewAddTaskSchedule) {
        _addTaskModel
            .update {
                it.copy(
                    taskSchedule = event.addTaskScheduleEventModel
                )
            }
        onClearTaskScheduleEventBus()
    }

    private fun onClearTaskScheduleEventBus() {
        viewModelScope
            .launch {
                taskScheduleEventBus.clearEvent(defaultEvent = TaskScheduleEvent.ClearEvent)
            }
    }

    private val observePriorityPickerEventBus = priorityPickerEventBus
        .subscribeOn<PriorityPickerEvent, PriorityPickerEvent.NewPriority>(
            scope = viewModelScope
        ) { event ->
            onAction(
                AddTaskDialogAction
                    .OnChangePriority(
                        priority = event.priorityEnum
                    )
            )
            priorityPickerEventBus.clearEvent(PriorityPickerEvent.ClearEvent)
        }

    private val observeHashtagPickerEventBus = hashtagPickerEventBus
        .subscribeOn<HashtagPickerEvent, HashtagPickerEvent.NewHashtags>(
            scope = viewModelScope
        ) { event ->
            onAction(
                AddTaskDialogAction
                    .OnChangeHashtags(
                        hashtags = event.hashtags
                    )
            )
            hashtagPickerEventBus.clearEvent(HashtagPickerEvent.ClearEvent)
        }


    val addTaskModelError: StateFlow<AddTaskModelError> = addTaskModel
        .map { currentModel ->
            addTaskModelValidator(currentModel)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddTaskModelError()
        )

    val canSave = addTaskModelError
        .map {
            canSaveAddTaskChecker(addTaskModelError = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val didModelChange = addTaskModel
        .map { currentModel ->
            addTaskModelChangeChecker(
                initialModel = trueInitialDialogState,
                currentModel = currentModel
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )


    fun onAction(action: AddTaskDialogAction) {

        when (action) {
            AddTaskDialogAction.OnSaveTask -> onSaveTask()
            AddTaskDialogAction.OnNavigateToPriorityPickerDialog -> onNavigateToPriorityPickerDialog()
            AddTaskDialogAction.OnNavigateBack -> onNavigateBack()
            AddTaskDialogAction.OnNavigateToHashtagPickerDialog -> onNavigateToHashtagPickerDialog()
            AddTaskDialogAction.OnRemoveTaskScheduleEvent -> onRemoveTaskScheduleEvent()
            is AddTaskDialogAction.OnNavigateToTaskSchedulePickerDialog -> onNavigateToTaskSchedulePickerDialog(
                action
            )

            is AddTaskDialogAction.OnChangeTitle -> onChangeTitle(action)
            is AddTaskDialogAction.OnChangeDescription -> onChangeDescription(action)
            is AddTaskDialogAction.OnChangePriority -> onChangePriority(action)
            is AddTaskDialogAction.OnChangeHashtags -> onChangeHashtags(action)
            AddTaskDialogAction.OnResetState -> onResetState()

        }
    }

    private fun onChangeTitle(action: AddTaskDialogAction.OnChangeTitle) {
        _addTaskModel.update {
            it.copy(
                title = action.title
            )
        }
    }

    private fun onChangeDescription(action: AddTaskDialogAction.OnChangeDescription) {
        _addTaskModel.update {
            it.copy(
                description = action.description
            )
        }
    }

    private fun onChangePriority(action: AddTaskDialogAction.OnChangePriority) {
        _addTaskModel.update {
            it.copy(
                priorityEnum = action.priority
            )
        }
    }

    private fun onChangeHashtags(action: AddTaskDialogAction.OnChangeHashtags) {
        _addTaskModel.update {
            it.copy(
                hashtags = action.hashtags
            )
        }
    }

    private fun onResetState() {
        _addTaskModel.value = AddTaskModel()
    }

    private fun onRemoveTaskScheduleEvent() {
        _addTaskModel.update {
            it.copy(
                taskSchedule = null
            )
        }
    }

    private fun onNavigateToTaskSchedulePickerDialog(action: AddTaskDialogAction.OnNavigateToTaskSchedulePickerDialog) {
        viewModelScope
            .launch {
                taskScheduleEventBus
                    .publish(
                        event = TaskScheduleEvent
                            .CurrentAddTaskSchedule(
                                addTaskScheduleEventModel = _addTaskModel.value.taskSchedule
                            )
                    )
            }
        viewModelScope
            .launch {
                addTaskDialogNavigationEventDispatcher
                    .dispatch(
                        event = AddTaskDialogNavigationEvent
                            .OnNavigateToTaskSchedulePickerDialog(
                                args = action.args
                            )
                    )
            }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            addTaskDialogNavigationEventDispatcher.dispatch(AddTaskDialogNavigationEvent.OnNavigateBack)
        }
    }

    private fun onNavigateToPriorityPickerDialog() {
        viewModelScope.launch {
            priorityPickerEventBus
                .publish(
                    PriorityPickerEvent
                        .CurrentPriority(
                            priorityEnum = addTaskModel.value.priorityEnum
                        )
                )
            addTaskDialogNavigationEventDispatcher.dispatch(AddTaskDialogNavigationEvent.OnNavigateToPriorityPickerDialog)
        }
    }

    private fun onNavigateToHashtagPickerDialog() {
        viewModelScope
            .launch {
                hashtagPickerEventBus
                    .publish(
                        HashtagPickerEvent
                            .CurrentHashtags(
                                hashtags = addTaskModel.value.hashtags
                            )
                    )
                addTaskDialogNavigationEventDispatcher.dispatch(
                    event = AddTaskDialogNavigationEvent.OnNavigateToHashtagPickerDialog
                )
            }
    }

    private fun onSaveTask() {
        viewModelScope.launch {
            useCaseOperator(
                loadingStatus = AddTaskLoadingStatus.STARTING.toUiText(),
                successMessageProvider = {
                    AddTaskSuccessMessage.TASK_SAVED.toUiText()
                },
                onSuccessAction = {
                    addTaskDialogNavigationEventDispatcher.dispatch(
                        AddTaskDialogNavigationEvent.OnNavigateBack
                    )
                },
            ) {
                addTaskUseCase(addTaskModel = addTaskModel.value)
            }
        }
    }


    companion object {
        const val SAVED_STATE_ADD_TASK_MODEL = "SAVED_STATE_TASK_MODEL"
    }

}