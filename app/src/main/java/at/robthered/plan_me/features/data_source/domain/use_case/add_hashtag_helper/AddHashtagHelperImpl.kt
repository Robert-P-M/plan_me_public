package at.robthered.plan_me.features.data_source.domain.use_case.add_hashtag_helper

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.datetime.Clock

class AddHashtagHelperImpl(
    private val localHashtagRepository: LocalHashtagRepository,
    private val localHashtagNameHistoryRepository: LocalHashtagNameHistoryRepository,
    private val clock: Clock = Clock.System,
) : AddHashtagHelper {
    override suspend operator fun invoke(uiHashtagModel: UiHashtagModel.NewHashTagModel): Long {

        val createdAt = clock.now()
        val hashtagModel = HashtagModel(
            name = uiHashtagModel.name,
            createdAt = createdAt,
            updatedAt = createdAt
        )

        val hashtagId = localHashtagRepository.insert(
            hashtagModel = hashtagModel
        )

        val hashtagNameHistoryModel = HashtagNameHistoryModel(
            hashtagId = hashtagId,
            text = uiHashtagModel.name,
            createdAt = createdAt
        )
        localHashtagNameHistoryRepository.insert(
            hashtagNameHistoryModel = hashtagNameHistoryModel
        )
        return hashtagId
    }

    override suspend operator fun invoke(uiHashtagModels: List<UiHashtagModel.NewHashTagModel>): List<Long> {
        val createdAt = clock.now()
        val hashtagModels = uiHashtagModels
            .map { addHashtagModel ->
                HashtagModel(
                    name = addHashtagModel.name,
                    createdAt = createdAt,
                    updatedAt = createdAt
                )
            }
        val ids = hashtagModels.map { hashtagModel ->
            val hashtagId = localHashtagRepository.insert(
                hashtagModel = hashtagModel
            )
            val hashtagNameHistoryModel = HashtagNameHistoryModel(
                hashtagId = hashtagId,
                text = hashtagModel.name,
                createdAt = createdAt
            )
            localHashtagNameHistoryRepository.insert(
                hashtagNameHistoryModel = hashtagNameHistoryModel
            )
            hashtagId

        }
        return ids
    }
}