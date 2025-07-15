package at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface CreateTaskCompletedHistoryUseCase {
    suspend operator fun invoke(
        taskId: Long,
        isCompleted: Boolean,
    ): AppResult<Unit, RoomDatabaseError>
}