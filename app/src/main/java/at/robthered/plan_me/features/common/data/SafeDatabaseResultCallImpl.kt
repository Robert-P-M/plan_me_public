package at.robthered.plan_me.features.common.data

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger

class SafeDatabaseResultCallImpl(
    private val appLogger: AppLogger,
    private val roomDatabaseErrorMapper: RoomDatabaseErrorMapper,
) : SafeDatabaseResultCall {
    override suspend fun <T> invoke(
        callerTag: String?,
        block: suspend () -> AppResult<T, RoomDatabaseError>,
    ): AppResult<T, RoomDatabaseError> {
        return try {
            block()
        } catch (e: Exception) {
            appLogger.e(
                tag = "SafeDatabaseResultCall - callerTag: $callerTag",
                msg = "A database operation failed for caller: $callerTag -> ${e.message}",
                throwable = e
            )
            val error = roomDatabaseErrorMapper(e)
            AppResult.Error(error)
        }
    }
}