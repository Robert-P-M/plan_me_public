package at.robthered.plan_me.features.common.domain.model

import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed class NotificationContent(
    // notificationId: Int = taskId.toInt()
    open val notificationId: Int,
    open val taskId: Long,
    open val title: String,
    open val description: String?,
    open val scheduledDate: LocalDate,
    open val triggerInstant: Instant,
    open val isFullDay: Boolean,
    open val priorityEnum: PriorityEnum?,
) {
    data class Default(
        override val notificationId: Int,
        override val taskId: Long,
        override val title: String,
        override val description: String?,
        override val scheduledDate: LocalDate,
        override val triggerInstant: Instant,
        override val priorityEnum: PriorityEnum?,
    ) : NotificationContent(
        notificationId = notificationId,
        taskId = taskId,
        title = title,
        description = description,
        scheduledDate = scheduledDate,
        triggerInstant = triggerInstant,
        isFullDay = false,
        priorityEnum = priorityEnum,
    )

    data class WithTime(
        override val notificationId: Int,
        override val taskId: Long,
        override val title: String,
        override val description: String?,
        override val scheduledDate: LocalDate,
        val scheduleStartTime: LocalTime,
        override val triggerInstant: Instant,
        override val priorityEnum: PriorityEnum?,
    ) : NotificationContent(
        notificationId = notificationId,
        taskId = taskId,
        title = title,
        description = description,
        scheduledDate = scheduledDate,
        triggerInstant = triggerInstant,
        isFullDay = false,
        priorityEnum = priorityEnum
    )

    data class WithDuration(
        override val notificationId: Int,
        override val taskId: Long,
        override val title: String,
        override val description: String?,
        override val scheduledDate: LocalDate,
        val scheduleStartTime: LocalTime,
        val scheduleEndTime: LocalTime,
        override val triggerInstant: Instant,
        override val priorityEnum: PriorityEnum?,
    ) : NotificationContent(
        notificationId = notificationId,
        taskId = taskId,
        title = title,
        description = description,
        scheduledDate = scheduledDate,
        triggerInstant = triggerInstant,
        isFullDay = false,
        priorityEnum = priorityEnum
    )
}