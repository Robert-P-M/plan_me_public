package at.robthered.plan_me.features.data_source.domain.model.task_statistics

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import kotlinx.datetime.Instant


sealed class TaskStatisticsModel(
    open val createdAt: Instant,
    open val isLast: Boolean,
) {
    data class TaskTitleHistory(
        val taskTitleHistoryModel: TaskTitleHistoryModel,
        override val createdAt: Instant,
        override val isLast: Boolean,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = isLast)

    data class TaskDescriptionHistory(
        val taskDescriptionHistoryModel: TaskDescriptionHistoryModel,
        override val createdAt: Instant,
        override val isLast: Boolean,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = isLast)

    data class TaskPriorityHistory(
        val taskPriorityHistoryModel: TaskPriorityHistoryModel,
        val previousTaskPriorityHistoryModel: TaskPriorityHistoryModel?,
        override val createdAt: Instant,
        override val isLast: Boolean,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = isLast)

    data class TaskCompletedHistory(
        val taskCompletedHistoryModel: TaskCompletedHistoryModel,
        override val createdAt: Instant,
        override val isLast: Boolean,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = isLast)

    data class TaskArchivedHistory(
        val taskArchivedHistoryModel: TaskArchivedHistoryModel,
        override val createdAt: Instant,
        override val isLast: Boolean,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = isLast)

    data class TaskInfo(
        val taskModel: TaskModel,
        override val createdAt: Instant,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = true)

    data class SubTask(
        val taskModel: TaskModel,
        override val createdAt: Instant,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = false)

    data class Hashtags(
        val hashtags: List<HashtagModel> = emptyList(),
        override val createdAt: Instant,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = true)

    data class ScheduleEvent(
        val taskScheduleEventModel: TaskScheduleEventModel,
        override val createdAt: Instant,
        override val isLast: Boolean,
        val isActiveScheduleEvent: Boolean = false,
    ) : TaskStatisticsModel(createdAt = createdAt, isLast = isLast)
}