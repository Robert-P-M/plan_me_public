package at.robthered.plan_me.features.data_source.domain.use_case.update_task_description

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import kotlinx.datetime.Instant

interface UpdateTaskDescriptionUseCase {
    suspend operator fun invoke(
        taskId: Long,
        description: String?,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError>
}