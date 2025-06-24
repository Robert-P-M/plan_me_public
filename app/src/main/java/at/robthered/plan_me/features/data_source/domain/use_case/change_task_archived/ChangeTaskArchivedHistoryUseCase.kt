package at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface ChangeTaskArchivedHistoryUseCase {
    suspend operator fun invoke(
        taskId: Long,
        isArchived: Boolean,
    ): AppResult<Unit, RoomDatabaseError>
}