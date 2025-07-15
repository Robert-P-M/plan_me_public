package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository

class DeleteTaskDescriptionHistoryUseCaseImpl(
    private val localTaskDescriptionHistoryRepository: LocalTaskDescriptionHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskDescriptionHistoryUseCase {
    override suspend operator fun invoke(taskDescriptionHistoryId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskDescriptionHistoryUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected =
                    localTaskDescriptionHistoryRepository.delete(taskDescriptionHistoryId = taskDescriptionHistoryId)
                if (rowsAffected == 0) {
                    AppResult.Error(error = RoomDatabaseError.NO_TASK_DESCRIPTION_HISTORY_FOUND)
                } else {
                    AppResult.Success(Unit)
                }
            }
        }
    }
}