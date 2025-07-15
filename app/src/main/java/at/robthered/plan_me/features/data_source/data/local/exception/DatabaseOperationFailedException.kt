package at.robthered.plan_me.features.data_source.data.local.exception

import at.robthered.plan_me.features.common.domain.RoomDatabaseError

class DatabaseOperationFailedException(val error: RoomDatabaseError, cause: Throwable? = null) :
    RuntimeException(cause)