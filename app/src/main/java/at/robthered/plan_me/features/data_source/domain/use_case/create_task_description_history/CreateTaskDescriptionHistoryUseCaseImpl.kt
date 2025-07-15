package at.robthered.plan_me.features.data_source.domain.use_case.create_task_description_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import kotlinx.datetime.Instant

class CreateTaskDescriptionHistoryUseCaseImpl(
    private val localTaskDescriptionHistoryRepository: LocalTaskDescriptionHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : CreateTaskDescriptionHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        description: String?,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {

            localTaskDescriptionHistoryRepository.insert(
                taskDescriptionHistoryModel = TaskDescriptionHistoryModel(
                    taskId = taskId,
                    text = description,
                    createdAt = createdAt
                )
            )
            AppResult.Success(Unit)
        }
    }
}