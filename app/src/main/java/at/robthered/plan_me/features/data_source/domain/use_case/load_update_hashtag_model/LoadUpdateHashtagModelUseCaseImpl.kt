package at.robthered.plan_me.features.data_source.domain.use_case.load_update_hashtag_model

import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadUpdateHashtagModelUseCaseImpl(
    private val localHashtagRepository: LocalHashtagRepository,
) : LoadUpdateHashtagModelUseCase {
    override suspend operator fun invoke(hashtagId: Long): Flow<UpdateHashtagModel> {
        return localHashtagRepository.get(hashtagId = hashtagId)
            .map {
                it?.toUpdateHashtagModel() ?: UpdateHashtagModel()
            }
    }
}