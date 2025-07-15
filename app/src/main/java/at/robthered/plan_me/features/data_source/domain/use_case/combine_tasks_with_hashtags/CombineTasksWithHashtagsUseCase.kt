package at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import kotlinx.coroutines.flow.Flow

interface CombineTasksWithHashtagsUseCase {
    operator fun invoke(
        tasks: List<TaskModel>,
    ): Flow<List<TaskWithUiHashtagsModel>>
}