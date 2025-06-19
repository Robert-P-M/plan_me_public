package at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import kotlinx.datetime.Instant

interface CreateTaskTitleHistoryUseCase {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError>
}