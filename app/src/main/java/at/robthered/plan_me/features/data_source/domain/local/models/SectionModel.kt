package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class SectionModel(
    val sectionId: Long = 0L,
    val title: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val hasSubtasks: Boolean = false,
)