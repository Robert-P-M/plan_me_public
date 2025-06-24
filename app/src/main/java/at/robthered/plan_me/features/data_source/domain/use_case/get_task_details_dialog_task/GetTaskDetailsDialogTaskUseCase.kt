package at.robthered.plan_me.features.data_source.domain.use_case.get_task_details_dialog_task

import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsAndChildrenModel
import kotlinx.coroutines.flow.Flow

interface GetTaskDetailsDialogTaskUseCase {
    operator fun invoke(
        taskId: Long,
        depth: Int,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<AppResource<TaskWithHashtagsAndChildrenModel>>
}