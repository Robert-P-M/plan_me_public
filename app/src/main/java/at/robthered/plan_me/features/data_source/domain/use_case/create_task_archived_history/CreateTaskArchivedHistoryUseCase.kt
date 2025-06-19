package at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface CreateTaskArchivedHistoryUseCase {
    suspend operator fun invoke(
        taskId: Long,
        isArchived: Boolean,
    ): AppResult<Unit, RoomDatabaseError>
}