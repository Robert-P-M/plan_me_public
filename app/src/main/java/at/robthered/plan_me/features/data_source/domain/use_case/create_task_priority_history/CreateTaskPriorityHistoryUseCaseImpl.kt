package at.robthered.plan_me.features.data_source.domain.use_case.create_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import kotlinx.datetime.Clock

class CreateTaskPriorityHistoryUseCaseImpl(
    private val localTaskPriorityHistoryRepository: LocalTaskPriorityHistoryRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock,
) : CreateTaskPriorityHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        priorityEnum: PriorityEnum?,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall {

            val now = clock.now()
            localTaskPriorityHistoryRepository.insert(
                taskPriorityHistoryModel = TaskPriorityHistoryModel(
                    taskId = taskId,
                    priorityEnum = priorityEnum,
                    createdAt = now
                )
            )
            AppResult.Success(Unit)
        }
    }
}