package at.robthered.plan_me.features.priority_picker_dialog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.robthered.plan_me.features.common.presentation.event_bus.subscribeOn
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.PriorityPickerDialogNavigationEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.navigation.PriorityPickerDialogNavigationEventDispatcher
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEvent
import at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event.PriorityPickerEventBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PriorityPickerDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val priorityPickerEventBus: PriorityPickerEventBus,
    private val priorityPickerDialogNavigationEventDispatcher: PriorityPickerDialogNavigationEventDispatcher,
) : ViewModel() {

    private val trueInitialPriorityEnum: PriorityEnum? by lazy {
        null
    }
    private val trueInitialTaskId: Long? by lazy {
        null
    }

    val appNavigationEvent = priorityPickerDialogNavigationEventDispatcher
        .navigationEvents
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 0
        )


    private val _currentPriority = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_CURRENT_PRIORITY,
            initialValue = trueInitialPriorityEnum
        )

    val currentPriority: StateFlow<PriorityEnum?> =
        _currentPriority.asStateFlow()

    private val _currentTaskId = savedStateHandle
        .getMutableStateFlow(
            key = SAVED_STATE_CURRENT_TASK_ID,
            initialValue = trueInitialTaskId
        )

    val currentTaskId: StateFlow<Long?> =
        _currentTaskId.asStateFlow()


    fun onAction(action: PriorityPickerDialogUiAction) {
        when (action) {
            is PriorityPickerDialogUiAction.OnPickPriority -> onPickPriority(action)
            PriorityPickerDialogUiAction.OnNavigateBack -> onNavigateBack()
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            priorityPickerDialogNavigationEventDispatcher
                .dispatch(
                    event = PriorityPickerDialogNavigationEvent
                        .OnNavigateBack
                )
        }
    }

    private fun onPickPriority(action: PriorityPickerDialogUiAction.OnPickPriority) {
        viewModelScope.launch {
            try {
                val taskId = currentTaskId.value
                if (taskId != null) {
                    priorityPickerEventBus.publish(
                        event = PriorityPickerEvent
                            .NewTaskPriority(
                                taskId = taskId,
                                priorityEnum = action.priorityEnum
                            )
                    )
                } else {
                    priorityPickerEventBus.publish(
                        event = PriorityPickerEvent
                            .NewPriority(
                                priorityEnum = action.priorityEnum
                            )
                    )
                }
            } catch (e: Exception) {

            } finally {
                _currentPriority.update { null }
                _currentTaskId.update { null }
                onNavigateBack()
            }

        }
    }

    private val _observeCurrentPriority = priorityPickerEventBus
        .subscribeOn<PriorityPickerEvent, PriorityPickerEvent.CurrentPriority>(
            scope = viewModelScope
        ) { event ->
            _currentPriority.update { event.priorityEnum }
        }

    private val _observeCurrentTaskPriority = priorityPickerEventBus
        .subscribeOn<PriorityPickerEvent, PriorityPickerEvent.CurrentTaskPriority>(
            scope = viewModelScope
        ) { event ->
            _currentPriority.update { event.priorityEnum }
            _currentTaskId.update { event.taskId }
        }

    companion object {
        const val SAVED_STATE_INITIAL_PRIORITY = "SAVED_STATE_INITIAL_PRIORITY"
        const val SAVED_STATE_CURRENT_PRIORITY = "SAVED_STATE_CURRENT_PRIORITY"
        const val SAVED_STATE_CURRENT_TASK_ID = "SAVED_STATE_CURRENT_TASK_ID"
    }

}