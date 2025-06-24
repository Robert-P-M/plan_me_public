package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.add_task.AddTaskModel
import kotlinx.datetime.Instant

fun AddTaskModel.toTaskModel(now: Instant): TaskModel {
    return TaskModel(
        parentTaskId = parentTaskId,
        sectionId = sectionId,
        title = title,
        description = description,
        isCompleted = false,
        isArchived = false,
        priorityEnum = priorityEnum,
        createdAt = now,
        updatedAt = now
    )
}