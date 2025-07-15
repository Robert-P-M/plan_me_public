package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_archived_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository

class DeleteTaskArchivedHistoryUseCaseImpl(
    private val localTaskArchivedHistoryRepository: LocalTaskArchivedHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskArchivedHistoryUseCase {
    override suspend operator fun invoke(taskArchivedHistoryId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskArchivedHistoryUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected =
                    localTaskArchivedHistoryRepository.delete(taskArchivedHistoryId = taskArchivedHistoryId)
                if (rowsAffected == 0)
                    return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_TASK_ARCHIVED_HISTORY_FOUND)
                AppResult.Success(Unit)
            }
        }
    }
}