package at.robthered.plan_me.features.data_source.domain.use_case.add_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel

interface AddTaskUseCase {
    suspend operator fun invoke(addTaskModel: AddTaskModel): AppResult<Unit, RoomDatabaseError>
}