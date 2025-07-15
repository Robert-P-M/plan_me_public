package at.robthered.plan_me.features.common.domain

sealed interface AppResult<out D, out E : AppError> {
    data class Success<out D>(val data: D) : AppResult<D, Nothing>
    data class Error<out E : AppError>(val error: E) : AppResult<Nothing, E>
}