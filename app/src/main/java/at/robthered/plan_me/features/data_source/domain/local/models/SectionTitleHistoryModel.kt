package at.robthered.plan_me.features.data_source.domain.local.models

import kotlinx.datetime.Instant

data class SectionTitleHistoryModel(
    val sectionTitleHistoryId: Long = 0L,
    val sectionId: Long,
    val text: String,
    val createdAt: Instant,
)