package at.robthered.plan_me.features.task_schedule_picker_dialog.presentation.task_schedule_event

import at.robthered.plan_me.features.common.domain.event_bus.EventBus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

interface TaskScheduleEventBus : EventBus<TaskScheduleEvent>

class TaskScheduleEventBusImpl : TaskScheduleEventBus {
    private val _events = MutableStateFlow<TaskScheduleEvent?>(null)
    override val events: Flow<TaskScheduleEvent?> = _events.asSharedFlow()

    override suspend fun publish(event: TaskScheduleEvent) {
        _events.emit(event)
    }
}