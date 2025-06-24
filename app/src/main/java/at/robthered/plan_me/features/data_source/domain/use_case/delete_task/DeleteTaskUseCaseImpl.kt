package at.robthered.plan_me.features.data_source.domain.use_case.delete_task

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository

class DeleteTaskUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskUseCase {
    override suspend operator fun invoke(taskId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected = localTaskRepository.delete(taskId = taskId)
                if (rowsAffected == 0)
                    return@runAsTransaction AppResult.Error(RoomDatabaseError.NO_TASK_FOUND)
                AppResult.Success(Unit)
            }
        }
    }
}