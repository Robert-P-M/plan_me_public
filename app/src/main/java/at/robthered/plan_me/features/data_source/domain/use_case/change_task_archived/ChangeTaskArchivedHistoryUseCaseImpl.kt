package at.robthered.plan_me.features.data_source.domain.use_case.change_task_archived

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Clock

class ChangeTaskArchivedHistoryUseCaseImpl(
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val localTaskRepository: LocalTaskRepository,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val clock: Clock,
) : ChangeTaskArchivedHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        isArchived: Boolean,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {

            val now = clock.now()
            val currentTask = getTaskModelUseCase(taskId)
                ?: return@safeDatabaseResultCall AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

            localTaskRepository
                .upsert(
                    taskModel = currentTask
                        .copy(
                            isArchived = isArchived,
                            updatedAt = now
                        )
                )

            AppResult.Success(Unit)
        }
    }
}