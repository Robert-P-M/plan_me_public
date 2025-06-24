package at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name

import at.robthered.plan_me.features.common.domain.AppResult
import at.robthered.plan_me.features.common.domain.RoomDatabaseError
import at.robthered.plan_me.features.common.domain.SafeDatabaseResultCall
import at.robthered.plan_me.features.data_source.domain.local.executor.TransactionProvider
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class UpdateHashtagNameUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val localHashtagRepository: LocalHashtagRepository,
    private val localHashtagNameHistoryRepository: LocalHashtagNameHistoryRepository,
    private val clock: Clock = Clock.System,
    private val safeDatabaseResultCall: SafeDatabaseResultCall,
) : UpdateHashtagNameUseCase {
    override suspend operator fun invoke(updateHashtagModel: UpdateHashtagModel): AppResult<Unit, RoomDatabaseError> {
        return safeDatabaseResultCall(
            callerTag = "UpdateHashtagNameUseCaseImpl"
        ) {
            transactionProvider.runAsTransaction {
                val createdAt = clock.now()
                val currentHashtagModel = localHashtagRepository
                    .get(hashtagId = updateHashtagModel.hashtagId).first()
                    ?: return@runAsTransaction AppResult.Error(error = RoomDatabaseError.NO_HASHTAG_FOUND)


                var hashtagToUpdate = currentHashtagModel.copy()
                if (currentHashtagModel.name != updateHashtagModel.name) {
                    hashtagToUpdate = hashtagToUpdate.copy(
                        name = updateHashtagModel.name,
                        updatedAt = createdAt
                    )
                    localHashtagRepository
                        .update(hashtagModel = hashtagToUpdate)
                    localHashtagNameHistoryRepository
                        .insert(
                            hashtagNameHistoryModel = HashtagNameHistoryModel(
                                hashtagId = hashtagToUpdate.hashtagId,
                                text = updateHashtagModel.name,
                                createdAt = createdAt
                            )
                        )
                }

                AppResult.Success(Unit)
            }
        }
    }
}