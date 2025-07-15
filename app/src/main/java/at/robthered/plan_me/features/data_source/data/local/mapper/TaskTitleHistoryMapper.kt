package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskTitleHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel

fun TaskTitleHistoryEntity.toModel(): TaskTitleHistoryModel {
    return TaskTitleHistoryModel(
        taskTitleHistoryId = this@toModel.taskTitleHistoryId,
        taskId = taskId,
        text = text,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskTitleHistoryEntity>.toModels(): List<TaskTitleHistoryModel> {
    return this.map { it.toModel() }
}

fun TaskTitleHistoryModel.toEntity(): TaskTitleHistoryEntity {
    return TaskTitleHistoryEntity(
        taskTitleHistoryId = taskTitleHistoryId,
        taskId = taskId,
        text = text,
        createdAt = createdAt.toLong()
    )
}

fun List<TaskTitleHistoryModel>.toEntities(): List<TaskTitleHistoryEntity> {
    return this.map { it.toEntity() }
}