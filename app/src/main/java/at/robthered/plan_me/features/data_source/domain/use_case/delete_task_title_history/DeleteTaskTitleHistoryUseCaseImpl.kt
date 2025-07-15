package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository

class DeleteTaskTitleHistoryUseCaseImpl(
    private val localTaskTitleHistoryRepository: LocalTaskTitleHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskTitleHistoryUseCase {
    override suspend operator fun invoke(taskTitleHistoryId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskTitleHistoryUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected =
                    localTaskTitleHistoryRepository.delete(taskTitleHistoryId = taskTitleHistoryId)
                if (rowsAffected == 0) {
                    AppResult.Error(error = RoomDatabaseError.NO_TASK_TITLE_HISTORY_FOUND)
                } else {
                    AppResult.Success(Unit)
                }
            }
        }
    }
}