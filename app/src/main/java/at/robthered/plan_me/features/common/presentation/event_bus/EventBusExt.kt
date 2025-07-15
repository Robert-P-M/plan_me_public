package at.robthered.plan_me.features.common.presentation.event_bus

import at.robthered.plan_me.features.common.domain.event_bus.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

suspend fun <T> EventBus<T>.clearEvent(defaultEvent: T) {
    publish(defaultEvent)
}


inline fun <T, reified E : T> EventBus<T>.subscribeOn(
    scope: CoroutineScope,
    crossinline action: suspend (E) -> Unit,
): Flow<E?> {
    return this.events
        .filterIsInstance<E>()
        .onEach { action(it) }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
}