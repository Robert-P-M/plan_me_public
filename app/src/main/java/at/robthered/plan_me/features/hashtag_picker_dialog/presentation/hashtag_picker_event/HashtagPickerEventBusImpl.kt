package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.hashtag_picker_event

import at.robthered.plan_me.features.common.domain.HashtagPickerEventBus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class HashtagPickerEventBusImpl : HashtagPickerEventBus {
    private val _events = MutableStateFlow<HashtagPickerEvent?>(null)
    override val events: Flow<HashtagPickerEvent?> =
        _events.asSharedFlow()

    override suspend fun publish(event: HashtagPickerEvent) {
        _events.emit(event)
    }
}