package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import kotlinx.coroutines.flow.Flow

interface LoadHashtagWithTasksUseCase {
    operator fun invoke(hashtagId: Long): Flow<HashtagWithTasksModel?>
}