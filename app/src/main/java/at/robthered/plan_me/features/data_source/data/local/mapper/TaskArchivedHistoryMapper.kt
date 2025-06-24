package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskArchivedHistoryEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel

fun TaskArchivedHistoryEntity.toModel(): TaskArchivedHistoryModel {
    return TaskArchivedHistoryModel(
        taskArchivedHistoryId = taskArchivedHistoryId,
        taskId = taskId,
        isArchived = isArchived,
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskArchivedHistoryEntity>.toModels(): List<TaskArchivedHistoryModel> {
    return this.map { it.toModel() }
}

fun TaskArchivedHistoryModel.toEntity(): TaskArchivedHistoryEntity {
    return TaskArchivedHistoryEntity(
        taskArchivedHistoryId = taskArchivedHistoryId,
        taskId = taskId,
        isArchived = isArchived,
        createdAt = createdAt.toLong(),
    )
}