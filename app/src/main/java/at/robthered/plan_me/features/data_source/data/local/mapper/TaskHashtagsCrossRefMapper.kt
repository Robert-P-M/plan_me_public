package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskHashtagsCrossRefEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel

fun TaskHashtagsCrossRefModel.toEntity(): TaskHashtagsCrossRefEntity {
    return TaskHashtagsCrossRefEntity(
        taskId = taskId,
        hashtagId = hashtagId,
        createdAt = createdAt.toLong()
    )
}

fun List<TaskHashtagsCrossRefModel>.toEntities(): List<TaskHashtagsCrossRefEntity> {
    return this.map { it.toEntity() }
}

fun TaskHashtagsCrossRefEntity.toModel(): TaskHashtagsCrossRefModel {
    return TaskHashtagsCrossRefModel(
        taskId = taskId,
        hashtagId = hashtagId,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskHashtagsCrossRefEntity>.toModels(): List<TaskHashtagsCrossRefModel> {
    return this.map { it.toModel() }
}