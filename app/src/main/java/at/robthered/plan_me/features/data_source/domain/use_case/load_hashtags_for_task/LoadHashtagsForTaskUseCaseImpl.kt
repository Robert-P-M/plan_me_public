package at.robthered.plan_me.features.data_source.domain.use_case.load_hashtags_for_task

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.coroutines.flow.Flow

class LoadHashtagsForTaskUseCaseImpl(
    private val localHashtagRepository: LocalHashtagRepository,
) : LoadHashtagsForTaskUseCase {
    override operator fun invoke(taskId: Long): Flow<List<HashtagModel>> {
        return localHashtagRepository.getHashtagsForTask(taskId = taskId)
    }
}