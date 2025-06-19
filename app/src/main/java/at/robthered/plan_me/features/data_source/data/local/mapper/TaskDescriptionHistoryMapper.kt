package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskDescriptionHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel

fun TaskDescriptionHistoryEntity.toModel(): TaskDescriptionHistoryModel {
    return TaskDescriptionHistoryModel(
        taskDescriptionHistoryId = this@toModel.taskDescriptionHistoryId,
        taskId = taskId,
        text = text,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskDescriptionHistoryEntity>.toModels(): List<TaskDescriptionHistoryModel> {
    return this.map { it.toModel() }
}

fun TaskDescriptionHistoryModel.toEntity(): TaskDescriptionHistoryEntity {
    return TaskDescriptionHistoryEntity(
        taskDescriptionHistoryId = taskDescriptionHistoryId,
        taskId = taskId,
        text = text,
        createdAt = createdAt.toLong(),
    )
}

fun List<TaskDescriptionHistoryModel>.toEntities(): List<TaskDescriptionHistoryEntity> {
    return this.map { it.toEntity() }
}