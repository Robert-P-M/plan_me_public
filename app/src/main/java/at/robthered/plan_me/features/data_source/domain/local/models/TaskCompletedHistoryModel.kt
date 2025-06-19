package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class TaskCompletedHistoryModel(
    val taskCompletedHistoryId: Long = 0L,
    val taskId: Long,
    val isCompleted: Boolean,
    val createdAt: Instant,
)