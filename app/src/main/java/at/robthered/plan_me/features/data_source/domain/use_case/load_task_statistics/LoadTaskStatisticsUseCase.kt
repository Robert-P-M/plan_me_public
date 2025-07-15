package at.robthered.plan_me.features.data_source.domain.use_case.load_task_statistics

import at.robthered.plan_me.features.common.domain.AppResource
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import kotlinx.coroutines.flow.Flow

interface LoadTaskStatisticsUseCase {
    operator fun invoke(taskId: Long): Flow<AppResource<List<TaskStatisticsModel>>>
}