package at.robthered.plan_me.features.data_source.domain.use_case.task_statistics

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel

interface TaskStatisticsBuilder {
    operator fun invoke(
        historyData: TaskHistoryModel,
        taskModel: TaskModel,
        subTasks: List<TaskModel>,
        finalHashtagModels: List<HashtagModel>,
    ): List<TaskStatisticsModel>
}