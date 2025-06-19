package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.SectionEntity
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.model.update_section_title.UpdateSectionTitleModel

fun SectionEntity.toModel(): SectionModel {
    return SectionModel(
        sectionId = sectionId,
        title = title,
        createdAt = createdAt.toInstant(),
        updatedAt = updatedAt.toInstant()
    )
}

fun List<SectionEntity>.toModels(): List<SectionModel> {
    return this.map { it.toModel() }
}

fun SectionModel.toEntity(): SectionEntity {
    return SectionEntity(
        sectionId = sectionId,
        title = title,
        createdAt = createdAt.toLong(),
        updatedAt = updatedAt.toLong()
    )
}

fun SectionModel.toUpdateSectionModel(): UpdateSectionTitleModel {
    return UpdateSectionTitleModel(
        sectionId = sectionId,
        title = title
    )
}