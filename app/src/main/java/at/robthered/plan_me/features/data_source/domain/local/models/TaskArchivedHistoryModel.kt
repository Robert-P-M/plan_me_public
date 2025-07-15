package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class TaskArchivedHistoryModel(
    val taskArchivedHistoryId: Long = 0L,
    val taskId: Long,
    val isArchived: Boolean,
    val createdAt: Instant,
)