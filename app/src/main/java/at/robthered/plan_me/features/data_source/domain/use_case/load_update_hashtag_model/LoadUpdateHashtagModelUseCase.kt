package at.robthered.plan_me.features.data_source.domain.use_case.load_update_hashtag_model

import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import kotlinx.coroutines.flow.Flow

interface LoadUpdateHashtagModelUseCase {
    suspend operator fun invoke(hashtagId: Long): Flow<UpdateHashtagModel>
}