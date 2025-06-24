package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel

fun HashtagEntity.toModel(): HashtagModel {
    return HashtagModel(
        hashtagId = hashtagId,
        name = name,
        createdAt = createdAt.toInstant(),
        updatedAt = updatedAt.toInstant(),
    )
}

fun HashtagModel.toEntity(): HashtagEntity {
    return HashtagEntity(
        hashtagId = hashtagId,
        name = name,
        createdAt = createdAt.toLong(),
        updatedAt = updatedAt.toLong()
    )
}

fun List<HashtagEntity>.toModels(): List<HashtagModel> {
    return this.map { it.toModel() }
}