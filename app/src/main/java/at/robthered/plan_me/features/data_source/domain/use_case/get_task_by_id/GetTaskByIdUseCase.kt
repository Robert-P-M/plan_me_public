package at.robthered.plan_me.features.data_source.domain.use_case.get_task_by_id

import at.robthered.plan_me.features.data_source.domain.model.TaskWithUiHashtagsModel
import kotlinx.coroutines.flow.Flow

interface GetTaskByIdUseCase {
    operator fun invoke(taskId: Long): Flow<TaskWithUiHashtagsModel?>
}