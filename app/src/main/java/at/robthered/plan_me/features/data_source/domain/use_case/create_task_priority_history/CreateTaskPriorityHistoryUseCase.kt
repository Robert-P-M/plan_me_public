package at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

interface CreateTaskPriorityHistoryUseCase {
    suspend operator fun invoke(
        taskId: Long,
        priorityEnum: PriorityEnum?,
    ): AppResult<Unit, RoomDatabaseError>
}