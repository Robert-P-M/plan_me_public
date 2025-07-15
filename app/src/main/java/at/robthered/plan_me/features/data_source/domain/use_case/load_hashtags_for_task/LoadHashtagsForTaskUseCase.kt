package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import kotlinx.coroutines.flow.Flow

interface LoadHashtagsForTaskUseCase {
    operator fun invoke(taskId: Long): Flow<List<HashtagModel>>
}