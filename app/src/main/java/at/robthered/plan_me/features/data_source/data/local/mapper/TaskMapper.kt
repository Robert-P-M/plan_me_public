package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.common.utils.ext.toInstant
import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel

fun TaskEntity.toModel(): TaskModel {
    return TaskModel(
        sectionId = sectionId,
        parentTaskId = parentTaskId,
        taskId = taskId,
        title = title,
        description = description,
        priorityEnum = priorityEnum,
        taskSchedule = taskSchedule?.toModel(),
        isCompleted = isCompleted,
        isArchived = isArchived,
        updatedAt = updatedAt.toInstant(),
        createdAt = createdAt.toInstant()
    )
}

fun List<TaskEntity>.toModels(): List<TaskModel> {
    return this.map { it.toModel() }
}

fun TaskModel.toEntity(): TaskEntity {
    return TaskEntity(
        sectionId = sectionId,
        parentTaskId = parentTaskId,
        taskId = taskId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        taskSchedule = taskSchedule?.toEntity(),
        isArchived = isArchived,
        priorityEnum = priorityEnum,
        createdAt = createdAt.toLong(),
        updatedAt = updatedAt.toLong()
    )
}