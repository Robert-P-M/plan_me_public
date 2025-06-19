package at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum

interface ChangeTaskPriorityUseCase {
    suspend operator fun invoke(
        priorityEnum: PriorityEnum?,
        taskId: Long,
    ): AppResult<Unit, RoomDatabaseError>
}