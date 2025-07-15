package at.robthered.plan_me.features.common.presentation.appEvent

import at.robthered.plan_me.features.common.presentation.uiModels.AppUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface AppUiEventDispatcher {
    val appUiEvent: SharedFlow<AppUiEvent?>
    val isLoading: Flow<Boolean>
    suspend fun dispatch(event: AppUiEvent?)
}