package at.robthered.plan_me.features.data_source.domain.use_case.get_task_history_helper

import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskHistoryModel
import kotlinx.coroutines.flow.Flow

interface GetTaskHistoryFlowHelper {
    operator fun invoke(taskId: Long): Flow<TaskHistoryModel>
}