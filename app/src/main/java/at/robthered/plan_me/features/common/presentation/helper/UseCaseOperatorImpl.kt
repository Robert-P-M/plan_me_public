package at.robthered.plan_me.features.common.presentation.helper

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.onError
import at.robthered.plan_me.features.common.domain.onSuccess
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.common.presentation.appEvent.AppUiEventDispatcher
import at.robthered.plan_me.features.common.presentation.uiModels.AppUiEvent
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger
import at.robthered.plan_me.features.data_source.presentation.ext.toUiText

class UseCaseOperatorImpl(
    private val appUiEventDispatcher: AppUiEventDispatcher,
    private val appLogger: AppLogger,
) : UseCaseOperator {
    override suspend operator fun <D> invoke(
        loadingStatus: UiText,
        successMessageProvider: (data: D) -> UiText,
        onSuccessAction: (suspend (D) -> Unit)?,
        useCase: suspend () -> AppResult<D, RoomDatabaseError>,
    ) {
        try {
            appUiEventDispatcher.dispatch(
                event = AppUiEvent.Loading(message = loadingStatus)
            )
            useCase()
                .onSuccess { data ->
                    appUiEventDispatcher.dispatch(
                        AppUiEvent.Success(
                            message = successMessageProvider(
                                data
                            )
                        )
                    )
                    onSuccessAction?.invoke(data)
                }
                .onError { error ->
                    appLogger.e("UseCaseOperator", "Operation failed: $error")
                    appUiEventDispatcher.dispatch(AppUiEvent.Error(message = error.toUiText()))
                }
        } catch (e: Exception) {
            appLogger.e("UseCaseOperator", "Unexpected error: ${e.message}", e)
            appUiEventDispatcher.dispatch(AppUiEvent.Error(message = RoomDatabaseError.UNKNOWN.toUiText()))
        } finally {
            appUiEventDispatcher.dispatch(null)
        }
    }
}