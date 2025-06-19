package at.robthered.plan_me.features.data_source.data.local.executor

import at.robthered.plan_me.features.data_source.data.local.exception.DatabaseOperationFailedException
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor

class SafeDatabaseExecutorImpl(
    private val roomDatabaseErrorMapper: RoomDatabaseErrorMapper,
) : SafeDatabaseExecutor {
    override suspend operator fun <T> invoke(
        execute: suspend () -> T,
    ): T {
        return try {
            execute()
        } catch (e: Exception) {
            val error = roomDatabaseErrorMapper(e)
            throw DatabaseOperationFailedException(
                error, e
            )
        }
    }
}