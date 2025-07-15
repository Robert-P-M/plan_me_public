package at.robthered.plan_me.features.data_source.domain.use_case.update_task_priority

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import kotlinx.datetime.Instant

interface UpdateTaskPriorityUseCase {
    suspend operator fun invoke(
        taskId: Long,
        priorityEnum: PriorityEnum?,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError>
}