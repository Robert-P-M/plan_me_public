package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import at.robthered.plan_me.features.common.domain.notification.AppAlarmScheduler
import at.robthered.plan_me.features.common.presentation.event_bus.clearEvent
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.common.presentation.navigation.Route
import at.robthered.plan_me.features.data_source.data.local.mapper.toAddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModelError
import at.robthered.plan_me.features.data_source.domain.validation.AddTaskScheduleEventModelValidator
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.CalendarMonthModel
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.Time
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.model.UpcomingDate
import at.robthered.plan_me.features.task_schedule_picker_dialog.domain.use_case.GetUpcomingDatesUseCase
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toInt
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.ext.toTimeOfDay
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.helper.DidAddTaskScheduleEventModelChangeChecker
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.TaskSchedulePickerDialogNavigationEvent
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.navigation.TaskSchedulePickerDialogNavigationEventDispatcher
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.paging.GetPagedMonthUseCase
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEvent
import at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event.TaskScheduleEventBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TaskSchedulePickerDialogViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getPagedMonthUseCase: GetPagedMonthUseCase,
    private val getUpcomingDatesUseCase: GetUpcomingDatesUseCase,
    private val taskScheduleEventBus: TaskScheduleEventBus,
    private val addTaskScheduleEventModelValidator: AddTaskScheduleEventModelValidator,
    private val didAddTaskScheduleEventModelChangeChecker: DidAddTaskScheduleEventModelChangeChecker,
    private val taskSchedulePickerDialogNavigationEventDispatcher: TaskSchedulePickerDialogNavigationEventDispatcher,
    private val appAlarmScheduler: AppAlarmScheduler,
) : ViewModel() {

    private val _hasExactAlarmPermission = MutableStateFlow(false)
    val hasExactAlarmPermission: StateFlow<Boolean> = _hasExactAlarmPermission


    fun checkPermission() {
        _hasExactAlarmPermission.value = appAlarmScheduler.canScheduleExactAlarms()
    }

    init {
        checkPermission()
    }

    private val args by lazy {
        savedStateHandle.toRoute<Route.TaskSchedulePickerDialog>()
    }

    private val trueInitialAddTaskScheduleEventModel by lazy {
        AddTaskScheduleEventModel(
            startDateInEpochDays = args.startDateInEpochDays,
            taskId = args.taskId,
            timeOfDayInMinutes = args.timeOfDayInMinutes,
            isNotificationEnabled = args.isNotificationEnabled,
            durationInMinutes = args.durationInMinutes,
            isFullDay = args.isFullDay,
        )
    }

    private val initialAddTaskScheduleModel =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_INITIAL_ADD_TASK_SCHEDULE_MODEL,
                initialValue = trueInitialAddTaskScheduleEventModel
            )

    val appNavigationEvent = taskSchedulePickerDialogNavigationEventDispatcher
        .navigationEvents
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 0
        )

    private val _addTaskScheduleEventModel: MutableStateFlow<AddTaskScheduleEventModel> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_ADD_TASK_SCHEDULE_MODEL,
                initialValue = initialAddTaskScheduleModel.value
            )

    val didModelChange = _addTaskScheduleEventModel
        .map { currentModel ->
            didAddTaskScheduleEventModelChangeChecker(
                currentModel = currentModel,
                initialModel = trueInitialAddTaskScheduleEventModel
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    private val _addTaskScheduleEventModelError =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_ADD_TASK_SCHEDULE_MODEL_ERROR,
                initialValue = AddTaskScheduleEventModelError()
            )


    val addTaskScheduleEventModelError: StateFlow<AddTaskScheduleEventModelError> =
        _addTaskScheduleEventModelError.asStateFlow()

    private val observeAddTaskScheduleEventModelForError = _addTaskScheduleEventModel
        .map { currentModel ->
            _addTaskScheduleEventModelError.update {
                addTaskScheduleEventModelValidator(addTaskScheduleEventModel = currentModel)
            }
        }
        .launchIn(viewModelScope)


    private val _initialLocalDate: MutableStateFlow<Int> =
        savedStateHandle
            .getMutableStateFlow(
                key = SAVED_STATE_HANDLE_INITIAL_LOCAL_DATE,
                initialValue = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    .toEpochDays()
            )

    val pickedDate: StateFlow<LocalDate?> = _addTaskScheduleEventModel
        .map {
            it.startDateInEpochDays?.let { days ->
                LocalDate.fromEpochDays(days)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = args.startDateInEpochDays?.let {
                LocalDate.fromEpochDays(it)
            }
        )

    val pickedTime: StateFlow<Time?> = _addTaskScheduleEventModel
        .map {
            it.timeOfDayInMinutes?.toTimeOfDay()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = args.timeOfDayInMinutes?.toTimeOfDay()
        )

    val pickedDuration: StateFlow<Time?> = _addTaskScheduleEventModel
        .map {
            it.durationInMinutes?.toTimeOfDay()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = args.durationInMinutes?.toTimeOfDay()
        )

    val isNotificationEnabled: StateFlow<Boolean> = _addTaskScheduleEventModel
        .map {
            it.isNotificationEnabled
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = args.isNotificationEnabled
        )

    val isFullDay: StateFlow<Boolean> = _addTaskScheduleEventModel
        .map {
            it.isFullDay
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = args.isFullDay
        )


    fun onAction(action: TaskSchedulePickerDialogUiAction) {
        when (action) {
            TaskSchedulePickerDialogUiAction.OnNavigateBack -> onNavigateBack()
            is TaskSchedulePickerDialogUiAction.OnPickLocalDate -> onPickLocalDate(action)
            is TaskSchedulePickerDialogUiAction.OnPickTime -> onPickTime(action)
            is TaskSchedulePickerDialogUiAction.OnToggleNotificationEnabled -> onToggleNotificationEnabled()
            is TaskSchedulePickerDialogUiAction.OnPickDuration -> onPickDuration(action)
            TaskSchedulePickerDialogUiAction.OnToggleIsFullDay -> onToggleIsFullDay()
            TaskSchedulePickerDialogUiAction.OnSaveTaskSchedule -> onSaveTaskSchedule()
        }
    }

    private fun onSaveTaskSchedule() {
        viewModelScope
            .launch {
                if (_addTaskScheduleEventModel.value.startDateInEpochDays == null) {
                    taskScheduleEventBus
                        .publish(
                            event = TaskScheduleEvent
                                .NewAddTaskSchedule(
                                    addTaskScheduleEventModel = null
                                )
                        )
                } else {
                    taskScheduleEventBus
                        .publish(
                            event = TaskScheduleEvent
                                .NewAddTaskSchedule(
                                    addTaskScheduleEventModel = _addTaskScheduleEventModel.value
                                )
                        )
                }

            }
        onNavigateBack()
    }

    private val observeCurrentAddTaskSchedule = taskScheduleEventBus
        .subscribeOn<TaskScheduleEvent, TaskScheduleEvent.CurrentTaskScheduleEventModel>(
            scope = viewModelScope,
        ) { event ->
            onSetCurrentAddTaskSchedule(event)
        }

    private fun onSetCurrentAddTaskSchedule(event: TaskScheduleEvent.CurrentTaskScheduleEventModel) {

        event.taskScheduleEventModel?.let {
            _addTaskScheduleEventModel.value = it.toAddTaskScheduleEventModel()
        }
        onClearTaskScheduleEventBus()
    }

    private fun onClearTaskScheduleEventBus() {
        viewModelScope
            .launch {
                taskScheduleEventBus.clearEvent(defaultEvent = TaskScheduleEvent.ClearEvent)
            }
    }

    private fun onToggleIsFullDay() {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    isFullDay = it.isFullDay.not(),
                    durationInMinutes = null,
                    timeOfDayInMinutes = null,
                )
            }
    }

    private fun onToggleNotificationEnabled() {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    isNotificationEnabled = it.isNotificationEnabled.not()
                )
            }
    }

    private fun onNavigateBack() {
        viewModelScope
            .launch {
                taskSchedulePickerDialogNavigationEventDispatcher
                    .dispatch(
                        event = TaskSchedulePickerDialogNavigationEvent.OnNavigateBack
                    )
            }
    }

    private fun onPickDuration(action: TaskSchedulePickerDialogUiAction.OnPickDuration) {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    durationInMinutes = action.duration?.toInt(),
                    isFullDay = false,
                )
            }
    }

    private fun onPickTime(action: TaskSchedulePickerDialogUiAction.OnPickTime) {
        if (action.time == null) {
            resetDuration()
        }
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    timeOfDayInMinutes = action.time?.toInt(),
                    isFullDay = false,
                )
            }
    }

    private fun onPickLocalDate(action: TaskSchedulePickerDialogUiAction.OnPickLocalDate) {
        if (action.localDate == null) {
            resetDate()
            resetTime()
            resetDuration()
            resetFullDay()
        } else {

            _addTaskScheduleEventModel
                .update {
                    it.copy(
                        startDateInEpochDays = action.localDate.toEpochDays()
                    )
                }
        }
    }

    private fun resetFullDay() {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    isFullDay = false
                )
            }
    }

    private fun resetDate() {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    startDateInEpochDays = null
                )
            }
    }

    private fun resetTime() {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    timeOfDayInMinutes = null
                )
            }
    }

    private fun resetDuration() {
        _addTaskScheduleEventModel
            .update {
                it.copy(
                    durationInMinutes = null
                )
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedCalendarMonth: StateFlow<PagingData<CalendarMonthModel>> = _initialLocalDate
        .flatMapLatest { epochDays ->
            getPagedMonthUseCase(initialLocalDate = LocalDate.fromEpochDays(epochDays))
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val upcomingDates: StateFlow<List<UpcomingDate>> = _initialLocalDate
        .flatMapLatest { epochDays ->
            getUpcomingDatesUseCase(localDate = LocalDate.fromEpochDays(epochDays))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    companion object {
        const val SAVED_STATE_HANDLE_INITIAL_LOCAL_DATE = "SAVED_STATE_HANDLE_INITIAL_LOCAL_DATE"
        const val SAVED_STATE_HANDLE_ADD_TASK_SCHEDULE_MODEL =
            "SAVED_STATE_HANDLE_ADD_TASK_SCHEDULE_MODEL"
        const val SAVED_STATE_HANDLE_INITIAL_ADD_TASK_SCHEDULE_MODEL =
            "SAVED_STATE_HANDLE_INITIAL_ADD_TASK_SCHEDULE_MODEL"
        const val SAVED_STATE_HANDLE_ADD_TASK_SCHEDULE_MODEL_ERROR =
            "SAVED_STATE_HANDLE_ADD_TASK_SCHEDULE_MODEL_ERROR"

    }

}