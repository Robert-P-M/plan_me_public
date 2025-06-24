package at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import at.robthered.plan_me.features.data_source.domain.use_case.get_task_models_for_parent_task.GetTaskModelsForParentUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class RecursiveTaskWithHashtagsAndChildrenModelHelperImpl(
    private val getTaskModelsForParentUseCase: GetTaskModelsForParentUseCase,
) : RecursiveTaskWithHashtagsAndChildrenModelHelper {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend operator fun invoke(
        parentTaskId: Long,
        currentDepth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithHashtagsAndChildrenModel>> {
        if (currentDepth < 0) {
            return flowOf(emptyList())
        }
        return getTaskModelsForParentUseCase(
            parentTaskId = parentTaskId,
            showCompleted = showCompleted,
            showArchived = showArchived,
            sortDirection = sortDirection,
        ).flatMapLatest { directChildren ->
            if (directChildren.isEmpty()) {
                flowOf(emptyList())
            } else {
                val grandChildrenFlows: List<Flow<TaskWithHashtagsAndChildrenModel>> =
                    directChildren.map { childrenModel ->
                        invoke(
                            parentTaskId = childrenModel.task.taskId,
                            currentDepth = currentDepth - 1,
                            showCompleted = showCompleted,
                            showArchived = showArchived,
                            sortDirection = sortDirection,
                        ).map { grandChildrenList ->
                            TaskWithHashtagsAndChildrenModel(
                                taskWithUiHashtagsModel = childrenModel,
                                children = grandChildrenList,
                                maxDepthReached = (currentDepth - 1) < 0,
                            )
                        }
                    }
                combine(grandChildrenFlows) { arrayOfGrandChildren ->
                    arrayOfGrandChildren.toList()
                }
            }
        }

    }
}