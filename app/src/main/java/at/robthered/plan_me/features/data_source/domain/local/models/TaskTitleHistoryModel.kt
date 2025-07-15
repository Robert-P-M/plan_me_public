package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class TaskTitleHistoryModel(
    val taskTitleHistoryId: Long = 0L,
    val taskId: Long,
    val text: String,
    val createdAt: Instant,
)