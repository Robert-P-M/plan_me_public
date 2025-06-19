package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class TaskScheduleEventModel(
    val taskScheduleEventId: Long = 0,
    val taskId: Long = 0,
    val startDateInEpochDays: Int,
    val timeOfDayInMinutes: Int? = null,
    val isNotificationEnabled: Boolean = false,
    val durationInMinutes: Int? = null,
    val isFullDay: Boolean = false,
    val createdAt: Instant,
)