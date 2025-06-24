package at.robthered.plan_me.features.data_source.domain.local.models

import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import kotlinx.datetime.Instant

data class TaskPriorityHistoryModel(
    val taskPriorityId: Long = 0L,
    val taskId: Long,
    val priorityEnum: PriorityEnum? = null,
    val createdAt: Instant,
)