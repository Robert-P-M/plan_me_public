package at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import kotlinx.coroutines.flow.Flow

interface GetRootTaskModelsUseCase {
    operator fun invoke(
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithUiHashtagsModel>>
}