package at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_model.GetTaskModelUseCase
import kotlinx.datetime.Clock

class ChangeTaskPriorityHistoryUseCaseImpl(
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val getTaskModelUseCase: GetTaskModelUseCase,
    private val localTaskRepository: LocalTaskRepository,
    private val clock: Clock,
) : ChangeTaskPriorityHistoryUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        priorityEnum: PriorityEnum?,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "ChangeTaskPriorityHistoryUseCaseImpl"
        ) {
            val now = clock.now()

            val taskModel = getTaskModelUseCase(taskId = taskId)
                ?: return@safeDatabaseResultCall AppResult.Error(error = RoomDatabaseError.NO_TASK_FOUND)

            localTaskRepository.upsert(
                taskModel.copy(
                    priorityEnum = priorityEnum,
                    updatedAt = now
                )
            )
            AppResult.Success(Unit)
        }
    }
}