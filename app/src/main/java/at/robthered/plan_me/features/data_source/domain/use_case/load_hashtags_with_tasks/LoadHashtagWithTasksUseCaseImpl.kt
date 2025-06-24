package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_with_tasks

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagWithTasksRelationRepository
import kotlinx.coroutines.flow.Flow

class LoadHashtagWithTasksUseCaseImpl(
    private val localHashtagWithTasksRelationRepository: LocalHashtagWithTasksRelationRepository,
) : LoadHashtagWithTasksUseCase {
    override operator fun invoke(hashtagId: Long): Flow<HashtagWithTasksModel?> {
        return localHashtagWithTasksRelationRepository
            .getHashtagWithTasks(hashtagId = hashtagId)
    }
}