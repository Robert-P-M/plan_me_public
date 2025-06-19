package at.robthered.plan_me.features.data_source.domain.use_case.create_task_archived_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import kotlinx.datetime.Clock

class CreateTaskArchivedHistoryUseCaseImpl(
    private val localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock,
) : CreateTaskArchivedHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        isArchived: Boolean,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {
            val now = clock.now()
            localTaskArchivedHistoryRepository
                .insert(
                    taskArchivedHistoryModel = TaskArchivedHistoryModel(
                        taskId = taskId,
                        isArchived = isArchived,
                        createdAt = now
                    )
                )

            AppResult.Success(Unit)
        }
    }
}