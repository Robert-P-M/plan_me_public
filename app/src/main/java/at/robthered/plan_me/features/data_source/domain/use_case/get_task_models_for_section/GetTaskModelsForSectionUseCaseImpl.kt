package at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_section

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn

class GetTaskModelsForSectionUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val combineTasksWithHashtagsUseCase: CombineTasksWithHashtagsUseCase,
) : GetTaskModelsForSectionUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(
        sectionId: Long,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithUiHashtagsModel>> {
        return localTaskRepository
            .getTaskModelsForSection(
                sectionId = sectionId,
                showCompleted = showCompleted,
                showArchived = showArchived,
                sortDirection = sortDirection,
            )
            .flatMapLatest { tasks -> combineTasksWithHashtagsUseCase(tasks) }
            .flowOn(Dispatchers.Default)
    }
}