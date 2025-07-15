package at.robthered.plan_me.features.data_source.domain.use_case.create_task_title_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import kotlinx.datetime.Instant

class CreateTaskTitleHistoryUseCaseImpl(
    private val localTaskTitleHistoryRepository: LocalTaskTitleHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : CreateTaskTitleHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        title: String,
        createdAt: Instant,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {
            localTaskTitleHistoryRepository
                .insert(
                    taskTitleHistoryModel = TaskTitleHistoryModel(
                        taskId = taskId,
                        text = title,
                        createdAt = createdAt
                    )
                )
            AppResult.Success(Unit)
        }
    }
}