package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository

class DeleteTaskPriorityHistoryUseCaseImpl(
    private val localTaskPriorityHistoryRepository: LocalTaskPriorityHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskPriorityHistoryUseCase {
    override suspend operator fun invoke(taskPriorityHistoryId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskPriorityHistoryUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected =
                    localTaskPriorityHistoryRepository.delete(taskPriorityHistoryId = taskPriorityHistoryId)
                if (rowsAffected == 0)
                    return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_PRIORITY_HISTORY_FOUND)
                AppResult.Success(Unit)
            }
        }
    }
}