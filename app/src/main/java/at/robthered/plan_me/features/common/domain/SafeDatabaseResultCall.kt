package at.robthered.plan_me.features.common.domain

interface SafeDatabaseResultCall {
    suspend operator fun <T> invoke(
        callerTag: String? = null,
        block: suspend () -> AppResult<T, RoomDatabaseError>,
    ): AppResult<T, RoomDatabaseError>
}