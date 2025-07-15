package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagNameHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel

fun HashtagNameHistoryEntity.toModel(): HashtagNameHistoryModel {
    return HashtagNameHistoryModel(
        hashtagNameHistoryId = hashtagNameHistoryId,
        hashtagId = hashtagId,
        text = text,
        createdAt = createdAt.toInstant()
    )
}

fun HashtagNameHistoryModel.toEntity(): HashtagNameHistoryEntity {
    return HashtagNameHistoryEntity(
        hashtagNameHistoryId = hashtagNameHistoryId,
        hashtagId = hashtagId,
        text = text,
        createdAt = createdAt.toLong()
    )
}

fun List<HashtagNameHistoryEntity>.toModels(): List<HashtagNameHistoryModel> {
    return this.map { it.toModel() }
}