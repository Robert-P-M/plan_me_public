package at.robthered.plan_me.features.data_source.domain.use_case.change_task_completed

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Clock

class ChangeTaskCompletedUseCaseImpl(
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val localTaskRepository: LocalTaskRepository,
    private val clock: Clock,
) : ChangeTaskCompletedUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        isCompleted: Boolean,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "ChangeTaskCompletedUseCaseImpl"
        ) {
            val now = clock.now()

            val taskModel = getTaskModelUseCase(taskId = taskId)
                ?: return@safeDatabaseResultCall AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)
            val taskToUpsert = taskModel.copy(
                isCompleted = isCompleted,
                updatedAt = now
            )

            localTaskRepository.upsert(taskToUpsert)
            AppResult.Success(Unit)
        }

    }
}