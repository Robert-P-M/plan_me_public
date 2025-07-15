package at.robthered.plan_me.features.data_source.domain.use_case.delete_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskUseCase {
    suspend operator fun invoke(taskId: Long): AppResult<Unit, RoomDatabaseError>
}