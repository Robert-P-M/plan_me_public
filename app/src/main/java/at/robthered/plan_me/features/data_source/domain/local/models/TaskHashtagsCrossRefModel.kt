package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class TaskHashtagsCrossRefModel(
    val taskId: Long,
    val hashtagId: Long,
    val createdAt: Instant,
)