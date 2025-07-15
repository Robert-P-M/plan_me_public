package at.robthered.plan_me.features.data_source.domain.use_case.update_task_description

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Instant

class UpdateTaskDescriptionUseCaseImpl(
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val localTaskRepository: LocalTaskRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : UpdateTaskDescriptionUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        description: String?,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "UpdateTaskDescriptionUseCaseImpl"
        ) {
            val currentTaskModel = getTaskModelUseCase(taskId = taskId)
                ?: return@safeDatabaseResultCall AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)
            localTaskRepository.upsert(
                taskModel = currentTaskModel
                    .copy(
                        description = description,
                        updatedAt = createdAt,
                    )
            )
            AppResult.Success(Unit)
        }

    }
}