package at.robthered.plan_me.features.common.domain

inline fun <T, E : AppError> AppResult<T, E>.onSuccess(
    action: (T) -> Unit,
): AppResult<T, E> {
    return when (this) {
        is AppResult.Error -> this
        is AppResult.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E : AppError> AppResult<T, E>.onError(
    action: (E) -> Unit,
): AppResult<T, E> {
    return when (this) {
        is AppResult.Error -> {
            action(error)
            this
        }

        is AppResult.Success -> this
    }
}

fun <T, E : AppError> AppResult<T, E>.getErrorOrNull(): E? {
    return when (this) {
        is AppResult.Success -> null
        is AppResult.Error -> this.error
    }
}

inline fun <T, E : AppError> AppResult<T, E>.onCompletion(
    action: () -> Unit,
): AppResult<T, E> {
    action()
    return this
}