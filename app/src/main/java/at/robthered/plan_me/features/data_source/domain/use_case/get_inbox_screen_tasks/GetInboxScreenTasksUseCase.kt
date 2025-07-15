package at.robthered.plan_me.features.data_source.domain.use_case.get_inbox_screen_tasks

import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import kotlinx.coroutines.flow.Flow

interface GetInboxScreenTasksUseCase {
    operator fun invoke(
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskWithHashtagsAndChildrenModel>>
}