package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository

class DeleteTaskCompletedHistoryUseCaseImpl(
    private val localTaskCompletedHistoryRepository: LocalTaskCompletedHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskCompletedHistoryUseCase {
    override suspend operator fun invoke(taskCompletedHistoryId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskCompletedHistoryUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected =
                    localTaskCompletedHistoryRepository.delete(taskCompletedHistoryId = taskCompletedHistoryId)
                if (rowsAffected > 0)
                    return@runAsTransaction AppResult.Success(Unit)

                AppResult.Error(error = RoomDatabaseError.NO_TASK_TITLE_HISTORY_FOUND)

            }
        }
    }
}