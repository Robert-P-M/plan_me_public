package at.robthered.plan_me.features.common.presentation.appEvent

import at.robthered.plan_me.features.common.presentation.uiModels.AppUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AppUiEventDispatcherImpl : AppUiEventDispatcher {
    private val _appUiEventChannel: MutableSharedFlow<AppUiEvent?> =
        MutableSharedFlow(replay = 1, extraBufferCapacity = 64)
    override val appUiEvent: SharedFlow<AppUiEvent?>
        get() = _appUiEventChannel
            .asSharedFlow()

    override suspend fun dispatch(event: AppUiEvent?) {
        _appUiEventChannel.emit(event)
    }

    override val isLoading: Flow<Boolean>
        get() = appUiEvent.map {
            it is AppUiEvent.Loading
                    || it == null && _appUiEventChannel.replayCache.lastOrNull() is AppUiEvent.Loading
        }
            .distinctUntilChanged()
}