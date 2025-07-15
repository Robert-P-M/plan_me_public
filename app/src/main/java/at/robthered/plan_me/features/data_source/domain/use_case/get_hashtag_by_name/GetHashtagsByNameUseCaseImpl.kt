package at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name

import at.robthered.plan_me.features.data_source.data.local.mapper.toFoundHashtagModels
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetHashtagsByNameUseCaseImpl(
    private val localHashtagRepository: LocalHashtagRepository,
) : GetHashtagsByNameUseCase {
    override operator fun invoke(query: String): Flow<List<UiHashtagModel.FoundHashtagModel>> {
        return localHashtagRepository.getByName(query).map { it.toFoundHashtagModels() }
    }
}