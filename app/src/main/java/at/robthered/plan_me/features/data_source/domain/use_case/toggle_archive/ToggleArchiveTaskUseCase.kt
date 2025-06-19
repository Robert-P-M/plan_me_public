package at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface ToggleArchiveTaskUseCase {
    suspend operator fun invoke(taskId: Long): AppResult<Unit, RoomDatabaseError>
}