package at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import at.robthered.plan_me.features.data_source.domain.use_case.combine_tasks_with_hashtags.CombineTasksWithHashtagsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class GetRootTaskModelsUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
    private val combineTasksWithHashtagsUseCase: CombineTasksWithHashtagsUseCase,
) : GetRootTaskModelsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithUiHashtagsModel>> {

        return localTaskRepository.getRootTaskModels(
            showCompleted = showCompleted,
            showArchived = showArchived,
            sortDirection = sortDirection,
        )
            .onStart { emit(emptyList()) }
            .flatMapLatest { tasks -> combineTasksWithHashtagsUseCase(tasks) }
            .flowOn(Dispatchers.Default)
    }
}