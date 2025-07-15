package at.robthered.plan_me.features.data_source.domain.local.exception


import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface RoomDatabaseErrorMapper {
    operator fun invoke(e: Exception): RoomDatabaseError
}