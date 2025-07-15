package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskScheduleEventEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel

fun TaskScheduleEventEntity.toModel(): TaskScheduleEventModel {
    return TaskScheduleEventModel(
        taskScheduleEventId = taskScheduleEventId,
        taskId = taskId,
        startDateInEpochDays = startDateInEpochDays,
        timeOfDayInMinutes = timeOfDayInMinutes,
        isNotificationEnabled = isNotificationEnabled,
        durationInMinutes = durationInMinutes,
        isFullDay = isFullDay,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskScheduleEventEntity>.toModels(): List<TaskScheduleEventModel> {
    return this.map { it.toModel() }
}

fun TaskScheduleEventModel.toEntity(): TaskScheduleEventEntity {
    return TaskScheduleEventEntity(
        taskScheduleEventId = taskScheduleEventId,
        taskId = taskId,
        startDateInEpochDays = startDateInEpochDays,
        timeOfDayInMinutes = timeOfDayInMinutes,
        isNotificationEnabled = isNotificationEnabled,
        durationInMinutes = durationInMinutes,
        isFullDay = isFullDay,
        createdAt = createdAt.toLong()
    )
}