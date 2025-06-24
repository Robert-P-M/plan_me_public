package at.robthered.plan_me.features.data_source.data.local.exception

import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDiskIOException
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.local.exception.RoomDatabaseErrorMapper
import at.robthered.plan_me.features.data_source.domain.local.utils.AppLogger

class RoomDatabaseErrorMapperImpl(
    private val appLogger: AppLogger,
) : RoomDatabaseErrorMapper {
    override operator fun invoke(e: Exception): RoomDatabaseError {
        appLogger.e(
            tag = "RoomDatabaseErrorMapperImpl",
            msg = "Database operation failed: ${e.message}",
            throwable = e
        )
        return when (e) {
            is SQLiteConstraintException -> {
                RoomDatabaseError.CONSTRAINT_VIOLATION
            }

            is SQLiteDiskIOException -> RoomDatabaseError.DISK_IO_ERROR
            is SQLiteDatabaseCorruptException -> RoomDatabaseError.CORRUPT_DATABASE
            is SQLiteCantOpenDatabaseException -> RoomDatabaseError.CANT_OPEN
            is IllegalStateException -> RoomDatabaseError.ILLEGAL_STATE
            else -> RoomDatabaseError.UNKNOWN
        }
    }
}