package at.robthered.plan_me.features.data_source.domain.use_case.recursive_task_with_hashtags_and_children

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import kotlinx.coroutines.flow.Flow

interface RecursiveTaskWithHashtagsAndChildrenModelHelper {
    suspend operator fun invoke(
        parentTaskId: Long,
        currentDepth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithHashtagsAndChildrenModel>>
}