package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskPriorityHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel

fun TaskPriorityHistoryEntity.toModel(): TaskPriorityHistoryModel {
    return TaskPriorityHistoryModel(
        taskPriorityId = taskPriorityId,
        taskId = taskId,
        priorityEnum = priorityEnum,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskPriorityHistoryEntity>.toModels(): List<TaskPriorityHistoryModel> {
    return this.map { it.toModel() }
}

fun TaskPriorityHistoryModel.toEntity(): TaskPriorityHistoryEntity {
    return TaskPriorityHistoryEntity(
        taskPriorityId = taskPriorityId,
        taskId = taskId,
        priorityEnum = priorityEnum,
        createdAt = createdAt.toLong()
    )
}

fun List<TaskPriorityHistoryModel>.toEntities(): List<TaskPriorityHistoryEntity> {
    return this.map { it.toEntity() }
}