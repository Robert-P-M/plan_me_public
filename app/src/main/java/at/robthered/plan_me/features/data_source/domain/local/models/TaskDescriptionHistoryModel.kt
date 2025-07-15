package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class TaskDescriptionHistoryModel(
    val taskDescriptionHistoryId: Long = 0L,
    val taskId: Long,
    val text: String? = null,
    val createdAt: Instant,
)