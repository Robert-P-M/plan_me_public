package at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import kotlinx.datetime.Instant

interface CreateTaskDescriptionHistoryUseCase {
    suspend operator fun invoke(
        taskId: Long,
        description: String?,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError>
}