package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskPriorityHistoryUseCase {
    suspend operator fun invoke(taskPriorityHistoryId: Long): AppResult<Unit, RoomDatabaseError>
}