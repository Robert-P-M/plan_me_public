package at.robthered.plan_me.features.common.presentation.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseNavigationEventDispatcher<T> : NavigationEventDispatcher<T> {
    private val _navigationChannel: Channel<T> =
        Channel<T>(Channel.Factory.BUFFERED)
    override val navigationEvents: Flow<T>
        get() = _navigationChannel
            .receiveAsFlow()

    override suspend fun dispatch(event: T) {
        _navigationChannel.send(event)
    }
}