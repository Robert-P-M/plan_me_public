package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskCompletedHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel

fun TaskCompletedHistoryEntity.toModel(): TaskCompletedHistoryModel {
    return TaskCompletedHistoryModel(
        taskCompletedHistoryId = taskCompletedHistoryId,
        taskId = taskId,
        isCompleted = isCompleted,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskCompletedHistoryEntity>.toModels(): List<TaskCompletedHistoryModel> {
    return this.map { it.toModel() }
}

fun TaskCompletedHistoryModel.toEntity(): TaskCompletedHistoryEntity {
    return TaskCompletedHistoryEntity(
        taskCompletedHistoryId = taskCompletedHistoryId,
        taskId = taskId,
        isCompleted = isCompleted,
        createdAt = createdAt.toLong()
    )
}