package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskTitleHistoryUseCase {
    suspend operator fun invoke(taskTitleHistoryId: Long): AppResult<Unit, RoomDatabaseError>
}