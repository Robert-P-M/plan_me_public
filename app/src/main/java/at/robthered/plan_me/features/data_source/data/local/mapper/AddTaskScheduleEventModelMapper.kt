package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.model.add_task_schedule_event.AddTaskScheduleEventModel
import kotlinx.datetime.Clock

fun TaskScheduleEventModel.toAddTaskScheduleEventModel(): AddTaskScheduleEventModel {
    return AddTaskScheduleEventModel(
        taskId = this.taskId,
        startDateInEpochDays = this.startDateInEpochDays,
        timeOfDayInMinutes = this.timeOfDayInMinutes,
        isNotificationEnabled = this.isNotificationEnabled,
        durationInMinutes = this.durationInMinutes,
        isFullDay = this.isFullDay
    )
}

fun AddTaskScheduleEventModel.toTaskScheduleEventModel(): TaskScheduleEventModel {
    return TaskScheduleEventModel(
        taskId = taskId,
        startDateInEpochDays = startDateInEpochDays ?: -1,
        timeOfDayInMinutes = timeOfDayInMinutes,
        isNotificationEnabled = isNotificationEnabled,
        durationInMinutes = durationInMinutes,
        isFullDay = isFullDay,
        createdAt = Clock.System.now()
    )
}