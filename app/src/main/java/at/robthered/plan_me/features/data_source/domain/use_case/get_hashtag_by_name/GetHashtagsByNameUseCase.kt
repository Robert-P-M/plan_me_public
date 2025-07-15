package at.robthered.plan_me.features.data_source.domain.use_case.get_hashtag_by_name

import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import kotlinx.coroutines.flow.Flow

interface GetHashtagsByNameUseCase {
    operator fun invoke(query: String): Flow<List<UiHashtagModel.FoundHashtagModel>>
}