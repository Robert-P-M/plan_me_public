package at.robthered.plan_me.features.common.domain.use_case

import at.robthered.plan_me.features.common.domain.model.NotificationContent
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class NotificationContentMapperImpl(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : NotificationContentMapper {
    override operator fun invoke(
        task: TaskModel,
    ): NotificationContent? {
        if (task.taskSchedule?.isNotificationEnabled == null) return null
        val taskDate = LocalDate.fromEpochDays(task.taskSchedule.startDateInEpochDays)
        val startInstant: Instant? =
            task.taskSchedule
                .timeOfDayInMinutes?.let { minutes ->
                    LocalDateTime(
                        date = taskDate,
                        time = LocalTime.Companion
                            .fromSecondOfDay(
                                secondOfDay = minutes * 60
                            )
                    )
                        .toInstant(timeZone)
                }

        return when {
            startInstant != null && task.taskSchedule.durationInMinutes != null -> {
                val endInstant = startInstant
                    .plus(
                        value = task.taskSchedule.durationInMinutes,
                        unit = DateTimeUnit.MINUTE
                    )
                NotificationContent.WithDuration(
                    notificationId = task.taskId.toInt(),
                    taskId = task.taskId,
                    title = task.title,
                    description = task.description,
                    scheduledDate = taskDate,
                    scheduleStartTime = startInstant.toLocalDateTime(timeZone).time,
                    scheduleEndTime = endInstant.toLocalDateTime(timeZone).time,
                    triggerInstant = startInstant,
                    priorityEnum = task.priorityEnum,
                )
            }

            startInstant != null -> {
                NotificationContent.WithTime(
                    notificationId = task.taskId.toInt(),
                    taskId = task.taskId,
                    title = task.title,
                    description = task.description,
                    scheduledDate = taskDate,
                    scheduleStartTime = startInstant.toLocalDateTime(timeZone).time,
                    triggerInstant = startInstant,
                    priorityEnum = task.priorityEnum,
                )
            }

            else -> {
                val triggerInstant = LocalDateTime(
                    date = taskDate,
                    time = LocalTime(
                        hour = 9, minute = 0
                    )
                )
                    .toInstant(
                        timeZone
                    )
                NotificationContent.Default(
                    notificationId = task.taskId.toInt(),
                    taskId = task.taskId,
                    title = task.title,
                    description = task.description,
                    scheduledDate = taskDate,
                    triggerInstant = triggerInstant,
                    priorityEnum = task.priorityEnum,
                )
            }
        }
    }


}