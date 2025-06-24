package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskArchivedHistoryUseCase {
    suspend operator fun invoke(taskArchivedHistoryId: Long): AppResult<Unit, RoomDatabaseError>
}