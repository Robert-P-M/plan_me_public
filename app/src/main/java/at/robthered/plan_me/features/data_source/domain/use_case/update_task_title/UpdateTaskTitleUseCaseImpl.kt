package at.robthered.plan_me.features.data_source.domain.use_case.update_task_title

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Instant

class UpdateTaskTitleUseCaseImpl(
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val localTaskRepository: LocalTaskRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : UpdateTaskTitleUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        title: String,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {

            val currentTaskModel = getTaskModelUseCase(taskId = taskId)
                ?: return@safeDatabaseResultCall AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)
            localTaskRepository.upsert(
                taskModel = currentTaskModel
                    .copy(
                        title = title,
                        updatedAt = createdAt,
                    )
            )
            AppResult.Success(Unit)
        }
    }
}