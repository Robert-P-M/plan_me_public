package at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import kotlinx.coroutines.flow.Flow

interface GetTaskModelsForParentUseCase {
    operator fun invoke(
        parentTaskId: Long,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithUiHashtagsModel>>
}