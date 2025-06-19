package at.robthered.plan_me.features.data_source.domain.use_case.task_statistics

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskHistoryModel
import at.robthered.plan_me.features.data_source.domain.model.task_statistics.TaskStatisticsModel
import kotlinx.datetime.Instant

class TaskStatisticsBuilderImpl : TaskStatisticsBuilder {
    override operator fun invoke(
        historyData: TaskHistoryModel,
        taskModel: TaskModel,
        subTasks: List<TaskModel>,
        finalHashtagModels: List<HashtagModel>,
    ): List<TaskStatisticsModel> {
        return buildList {
            addAll(historyData.title.mapIndexed { i, it ->
                TaskStatisticsModel.TaskTitleHistory(
                    it,
                    it.createdAt,
                    i == historyData.title.lastIndex
                )
            })
            addAll(historyData.taskSchedules.mapIndexed { i, it ->
                TaskStatisticsModel.ScheduleEvent(
                    it,
                    createdAt = it.createdAt,
                    i == historyData.taskSchedules.lastIndex,
                    isActiveScheduleEvent = it.copy(
                        taskScheduleEventId = 0,
                        taskId = 0,
                        createdAt = Instant.DISTANT_PAST,
                    ) == taskModel.taskSchedule?.copy(
                        taskScheduleEventId = 0,
                        taskId = 0,
                        createdAt = Instant.DISTANT_PAST,
                    )
                )
            })
            addAll(historyData.description.mapIndexed { i, it ->
                TaskStatisticsModel.TaskDescriptionHistory(
                    it,
                    it.createdAt,
                    i == historyData.description.lastIndex
                )
            })
            addAll(historyData.priority.mapIndexed { i, it ->
                val previous = historyData.priority.getOrNull(i + 1)
                TaskStatisticsModel.TaskPriorityHistory(
                    taskPriorityHistoryModel = it,
                    previousTaskPriorityHistoryModel = previous,
                    createdAt = it.createdAt,
                    isLast = i == historyData.priority.lastIndex
                )
            })
            addAll(historyData.completed.mapIndexed { i, it ->
                TaskStatisticsModel.TaskCompletedHistory(
                    it,
                    it.createdAt,
                    i == historyData.completed.lastIndex
                )
            })
            addAll(historyData.archived.mapIndexed { i, it ->
                TaskStatisticsModel.TaskArchivedHistory(
                    it,
                    it.createdAt,
                    i == historyData.archived.lastIndex
                )
            })
            addAll(subTasks.mapIndexed { i, it ->
                TaskStatisticsModel.SubTask(taskModel = it, createdAt = it.createdAt)
            })
            add(TaskStatisticsModel.TaskInfo(taskModel, taskModel.createdAt))
            val groupedHashtags = finalHashtagModels
                .groupBy { hashtag ->
                    val timestamp = hashtag.createdAt.toEpochMilliseconds()
                    val windowMillis: Long = 10 * 60 * 1000
                    (timestamp - (timestamp % windowMillis)).toInstant()
                }
            groupedHashtags.forEach { (createdAt, hashtags) ->
                add(
                    TaskStatisticsModel.Hashtags(
                        hashtags = hashtags,
                        createdAt = createdAt,
                    )
                )
            }
        }
    }
}