package at.robthered.plan_me.features.common.domain.event_bus

import kotlinx.coroutines.flow.Flow

interface EventBus<T> {
    val events: Flow<T?>
    suspend fun publish(event: T)
}