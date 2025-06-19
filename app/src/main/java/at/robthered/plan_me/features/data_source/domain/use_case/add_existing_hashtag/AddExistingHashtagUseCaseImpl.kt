package at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import kotlinx.datetime.Clock

class AddExistingHashtagUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
) : AddExistingHashtagUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        uiHashtagModel: UiHashtagModel.ExistingHashtagModel,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "AddExistingHashtagUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val createdAt = clock.now()
                val taskHashtagsCrossRefModel = TaskHashtagsCrossRefModel(
                    taskId = taskId,
                    hashtagId = uiHashtagModel.hashtagId,
                    createdAt = createdAt
                )
                localTaskHashtagsCrossRefRepository
                    .insert(crossRef = taskHashtagsCrossRefModel)
                AppResult.Success(Unit)
            }
        }
    }
}