package at.robthered.plan_me.features.priority_picker_dialog.presentation.priority_picker_event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class PriorityPickerEventBusImpl : PriorityPickerEventBus {
    private val _events = MutableStateFlow<PriorityPickerEvent?>(null)
    override val events: Flow<PriorityPickerEvent?> = _events.asSharedFlow()

    override suspend fun publish(event: PriorityPickerEvent) {
        _events.emit(event)
    }
}