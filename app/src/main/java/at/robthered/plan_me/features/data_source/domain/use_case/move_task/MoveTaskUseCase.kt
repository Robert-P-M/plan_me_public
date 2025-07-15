package at.robthered.plan_me.features.data_source.domain.use_case.move_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskUseCaseArgs

interface MoveTaskUseCase {
    suspend operator fun invoke(moveTaskUseCaseArgs: MoveTaskUseCaseArgs): AppResult<Unit, RoomDatabaseError>
}