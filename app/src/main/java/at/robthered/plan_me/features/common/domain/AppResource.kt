package at.robthered.plan_me.features.common.domain

sealed interface AppResource<out T> {
    data object Stale : AppResource<Nothing>
    data class Loading(val loadingStatus: LoadingStatus? = null) : AppResource<Nothing>
    data class Success<T>(val data: T) : AppResource<T>
    data class Error(val error: AppError) : AppResource<Nothing>
}