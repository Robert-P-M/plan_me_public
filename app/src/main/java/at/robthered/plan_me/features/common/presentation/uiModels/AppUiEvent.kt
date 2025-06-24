package at.robthered.plan_me.features.common.presentation.uiModels

import at.robthered.plan_me.features.common.presentation.UiText

sealed class AppUiEvent {
    data class Loading(val message: UiText) : AppUiEvent()
    data class Success(val message: UiText) : AppUiEvent()
    data class Error(val message: UiText) : AppUiEvent()
}