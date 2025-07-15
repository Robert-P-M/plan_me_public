package at.robthered.plan_me.features.data_source.domain.use_case.update_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel

interface UpdateTaskUseCase {
    suspend operator fun invoke(updateTaskModel: UpdateTaskModel): AppResult<Unit, RoomDatabaseError>
}