package at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper.AddHashtagHelper
import kotlinx.datetime.Clock


class AddNewHashtagUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localTaskHashtagsCrossRefRepository: LocalTaskHashtagsCrossRefRepository,
    private val addHashtagHelper: AddHashtagHelper,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
    private val clock: Clock = Clock.System,
) : AddNewHashtagUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        uiHashtagModel: UiHashtagModel.NewHashTagModel,
    ): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "AddNewHashtagUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val now = clock.now()
                val id = addHashtagHelper(uiHashtagModel = uiHashtagModel)
                val crossRef = TaskHashtagsCrossRefModel(
                    taskId = taskId,
                    hashtagId = id,
                    createdAt = now
                )
                localTaskHashtagsCrossRefRepository
                    .insert(crossRef = crossRef)
                AppResult.Success(Unit)
            }
        }
    }
}