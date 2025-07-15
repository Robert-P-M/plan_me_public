package at.robthered.plan_me.features.data_source.domain.model.task_statistics

import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel

data class TaskHistoryModel(
    val title: List<TaskTitleHistoryModel>,
    val description: List<TaskDescriptionHistoryModel>,
    val priority: List<TaskPriorityHistoryModel>,
    val completed: List<TaskCompletedHistoryModel>,
    val archived: List<TaskArchivedHistoryModel>,
    val taskSchedules: List<TaskScheduleEventModel> = emptyList(),
)