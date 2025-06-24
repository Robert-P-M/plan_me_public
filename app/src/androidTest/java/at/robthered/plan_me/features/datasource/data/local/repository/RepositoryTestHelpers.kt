package at.robthered.plan_me.features.datasource.data.local.repository

import at.robthered.plan_me.features.common.utils.ext.toLong
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import at.robthered.plan_me.features.data_source.domain.local.models.SectionModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import kotlinx.datetime.Instant

fun createTaskEntity(
    id: Long,
    title: String,
    createdAt: Long = Instant.parse("2025-01-01T12:00:00Z").toLong(),
    updatedAt: Long = Instant.parse("2025-01-01T12:00:00Z").toLong(),
    isCompleted: Boolean = false,
    parentTaskId: Long? = null,
    isArchived: Boolean = false,
    sectionId: Long? = null,
) = TaskEntity(
    taskId = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    parentTaskId = parentTaskId,
    isCompleted = isCompleted,
    isArchived = isArchived,
    sectionId = sectionId
)

fun createHashtag(id: Long, name: String) =
    HashtagEntity(hashtagId = id, name = name, createdAt = 0L, updatedAt = 0L)

fun createTaskModel(
    id: Long, title: String, sectionId: Long? = null,
    isCompleted: Boolean = false,
    isArchived: Boolean = false,
    parentTaskId: Long? = null,
) = TaskModel(
    taskId = id,
    title = title,
    createdAt = Instant.parse("2025-01-01T12:00:00Z"),
    updatedAt = Instant.parse("2025-01-01T12:00:00Z"),
    parentTaskId = parentTaskId,
    isCompleted = isCompleted,
    isArchived = isArchived,
    sectionId = sectionId
)

fun createSectionModel(id: Long, title: String) = SectionModel(
    sectionId = id,
    title = title,
    createdAt = Instant.parse("2025-01-01T12:00:00Z"),
    updatedAt = Instant.parse("2025-01-01T12:00:00Z")
)