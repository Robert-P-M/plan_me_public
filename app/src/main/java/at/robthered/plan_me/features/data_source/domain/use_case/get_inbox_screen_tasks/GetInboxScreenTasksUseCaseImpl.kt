package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.use_case.get_root_task_models.GetRootTaskModelsUseCase
import at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children.RecursiveTaskWithHashtagsAndChildrenModelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetInboxScreenTasksUseCaseImpl(
    private val getRootTaskModelsUseCase: GetRootTaskModelsUseCase,
    private val recursiveTaskWithHashtagsAndChildrenModelHelper: RecursiveTaskWithHashtagsAndChildrenModelHelper,

    ) : GetInboxScreenTasksUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithHashtagsAndChildrenModel>> {
        return getRootTaskModelsUseCase(
            showCompleted = showCompleted,
            showArchived = showArchived,
            sortDirection = sortDirection
        )
            .flatMapLatest { rootTasksWithHashtags ->
                if (rootTasksWithHashtags.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val taskWithChildrenFlows = rootTasksWithHashtags.map { taskWithHashtags ->
                        recursiveTaskWithHashtagsAndChildrenModelHelper(
                            parentTaskId = taskWithHashtags.task.taskId,
                            currentDepth = depth - 1,
                            showCompleted = showCompleted,
                            showArchived = showArchived,
                            sortDirection = sortDirection
                        ).map { childrenList ->
                            TaskWithHashtagsAndChildrenModel(
                                taskWithUiHashtagsModel = taskWithHashtags,
                                children = childrenList,
                                maxDepthReached = (depth - 1) < 0
                            )
                        }
                    }
                    combine(taskWithChildrenFlows) { it.toList() }
                }
            }
            .flowOn(Dispatchers.Default)
    }
}