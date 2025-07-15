package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError

interface DeleteTaskDescriptionHistoryUseCase {
    suspend operator fun invoke(taskDescriptionHistoryId: Long): AppResult<Unit, RoomDatabaseError>
}