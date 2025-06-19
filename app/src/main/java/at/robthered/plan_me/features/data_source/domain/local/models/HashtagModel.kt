package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class HashtagModel(
    val hashtagId: Long = 0L,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)