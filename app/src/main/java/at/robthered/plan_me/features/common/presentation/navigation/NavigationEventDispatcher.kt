package at.robthered.plan_me.features.common.presentation.navigation

import kotlinx.coroutines.flow.Flow

interface NavigationEventDispatcher<T> {
    val navigationEvents: Flow<T>
    suspend fun dispatch(event: T)
}