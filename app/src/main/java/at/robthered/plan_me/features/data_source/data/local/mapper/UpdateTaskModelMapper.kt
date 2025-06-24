package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel

fun TaskModel.toUpdateTaskModel(): UpdateTaskModel {
    return UpdateTaskModel(
        sectionId = sectionId,
        parentTaskId = parentTaskId,
        taskId = taskId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        isArchived = isArchived,
        priorityEnum = priorityEnum,
        hashtags = emptyList()
    )
}