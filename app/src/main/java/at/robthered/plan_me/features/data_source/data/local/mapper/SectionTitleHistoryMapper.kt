package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.SectionTitleHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.SectionTitleHistoryModel

fun SectionTitleHistoryEntity.toModel(): SectionTitleHistoryModel {
    return SectionTitleHistoryModel(
        sectionTitleHistoryId = sectionTitleHistoryId,
        sectionId = sectionId,
        text = text,
        createdAt = createdAt.toInstant()
    )
}

fun List<SectionTitleHistoryEntity>.toModels(): List<SectionTitleHistoryModel> {
    return this.map { it.toModel() }
}

fun SectionTitleHistoryModel.toEntity(): SectionTitleHistoryEntity {
    return SectionTitleHistoryEntity(
        sectionTitleHistoryId = sectionTitleHistoryId,
        sectionId = sectionId,
        text = text,
        createdAt = createdAt.toLong()
    )
}