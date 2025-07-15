package at.robthered.plan_me.features.common.presentation.helper

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.presentation.UiText

interface UseCaseOperator {
    suspend operator fun <D> invoke(
        loadingStatus: UiText,
        successMessageProvider: (data: D) -> UiText,
        onSuccessAction: (suspend (data: D) -> Unit)? = null,
        useCase: suspend () -> AppResult<D, RoomDatabaseError>,
    )
}