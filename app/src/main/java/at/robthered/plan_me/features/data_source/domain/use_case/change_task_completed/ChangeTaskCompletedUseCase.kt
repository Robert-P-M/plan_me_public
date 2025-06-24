package at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface ChangeTaskCompletedUseCase {
    suspend operator fun invoke(
        taskId: Long,
        isCompleted: Boolean,
    ): AppResult<Unit, RoomDatabaseError>
}