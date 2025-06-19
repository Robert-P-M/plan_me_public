package at.robthered.plan_me.features.data_source.domain.local.models

import at.robthered.plan_me.features.data_source.domain.model.priority.PriorityEnum
import kotlinx.datetime.Instant

data class TaskModel(
    val sectionId: Long? = null,
    val parentTaskId: Long? = null,
    val taskId: Long = 0L,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean,
    val isArchived: Boolean,
    val priorityEnum: PriorityEnum? = null,
    val taskSchedule: TaskScheduleEventModel? = null,
    val updatedAt: Instant,
    val createdAt: Instant,
    val hasSubtasks: Boolean = false,
)