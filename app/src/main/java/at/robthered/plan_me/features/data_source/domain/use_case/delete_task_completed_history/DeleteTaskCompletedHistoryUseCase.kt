package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskCompletedHistoryUseCase {
    suspend operator fun invoke(taskCompletedHistoryId: Long): AppResult<Unit, RoomDatabaseError>
}