package at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository

class DeleteTaskHashtagReferenceUseCaseImpl(
    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository,
    private val transactionProvider: TransactionProvider,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : DeleteTaskHashtagReferenceUseCase {
    override suspend fun invoke(taskId: Long, hashtagId: Long): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "DeleteTaskHashtagReferenceUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val rowsAffected = localTaskHashtagsCrossRefRepository.deleteCrossRef(
                    taskId = taskId,
                    hashtagId = hashtagId
                )
                if (rowsAffected == 0)
                    return@runAsTransaction AppResult.Error(RoomDatabaseError.NO_TASK_HASHTAG_CROSS_REF_FOUND)

                AppResult.Success(Unit)

            }
        }
    }
}