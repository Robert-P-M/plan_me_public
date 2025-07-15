package at.robthered.plan_me.features.data_source.domain.use_case.create_task_completed_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import kotlinx.datetime.Clock

class CreateTaskCompletedHistoryUseCaseImpl(
    private val localTaskCompletedHistoryRepository: LocalTaskCompletedHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock,
) : CreateTaskCompletedHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        isCompleted: Boolean,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "CreateTaskCompletedHistoryUseCaseImpl"
        ) {
            val now = clock.now()
            localTaskCompletedHistoryRepository
                .insert(
                    taskCompletedHistoryModel = TaskCompletedHistoryModel(
                        taskId = taskId,
                        isCompleted = isCompleted,
                        createdAt = now
                    )
                )
            AppResult.Success(Unit)
        }
    }
}