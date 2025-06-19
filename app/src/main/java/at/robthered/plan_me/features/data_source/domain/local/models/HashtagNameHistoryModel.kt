package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class HashtagNameHistoryModel(
    val hashtagNameHistoryId: Long = 0L,
    val hashtagId: Long,
    val text: String,
    val createdAt: Instant,
)