package at.robthered.plan_me.features.data_source.domain.use_case.update_task_title

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import kotlinx.datetime.Instant

interface UpdateTaskTitleUseCase {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError>
}