package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class LocalTaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskRepository {
    override suspend fun insert(taskModel: TaskModel): Long {
        return safeDatabaseExecutor {
            taskDao
                .insert(
                    taskEntity = taskModel.toEntity()
                )
        }
    }

    override suspend fun update(taskModel: TaskModel) {
        return safeDatabaseExecutor {
            taskDao
                .update(
                    taskEntity = taskModel.toEntity()
                )
        }
    }

    override suspend fun upsert(taskModel: TaskModel) {
        return safeDatabaseExecutor {
            taskDao
                .upsert(
                    taskEntity = taskModel.toEntity()
                )
        }
    }

    override suspend fun delete() {
        return safeDatabaseExecutor {
            taskDao
                .delete()
        }
    }

    override suspend fun delete(taskId: Long): Int {
        return safeDatabaseExecutor {
            taskDao
                .delete(
                    taskId = taskId
                )
        }
    }

    override suspend fun delete(taskIds: List<Long>) {
        return safeDatabaseExecutor {
            taskDao
                .delete(
                    taskIds = taskIds
                )
        }
    }

    override fun getTaskModelById(taskId: Long): Flow<TaskModel?> {
        return taskDao.getTaskEntityById(taskId).map { it?.toModel() }
    }

    override fun getRootTaskModels(
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskModel>> {
        val entitiesFlow = when (sortDirection) {
            SortDirection.ASC -> taskDao.getRootTaskEntitiesOrderAsc(
                showCompleted = showCompleted,
                showArchived = showArchived
            )

            SortDirection.DESC -> taskDao.getRootTaskEntitiesOrderDesc(
                showCompleted = showCompleted,
                showArchived = showArchived
            )
        }
        return entitiesFlow.map { it.toModels() }
    }

    override fun getTaskModelsForSection(
        sectionId: Long,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskModel>> {
        val entitiesFlow = when (sortDirection) {
            SortDirection.ASC -> taskDao.getTaskEntitiesForSectionOrderAsc(
                sectionId = sectionId,
                showCompleted = showCompleted,
                showArchived = showArchived
            )

            SortDirection.DESC -> taskDao.getTaskEntitiesForSectionOrderDesc(
                sectionId = sectionId,
                showCompleted = showCompleted,
                showArchived = showArchived
            )
        }
        return entitiesFlow.map { it.toModels() }
    }

    override fun getTaskModelsForParent(
        parentTaskId: Long,
        showCompleted: Boolean?,
        showArchived: Boolean?,
        sortDirection: SortDirection,
    ): Flow<List<TaskModel>> {
        val entitiesFlow =
            when (sortDirection) {
                SortDirection.ASC -> taskDao.getTaskEntitiesForParentOrderAsc(
                    parentTaskId = parentTaskId,
                    showCompleted = showCompleted,
                    showArchived = showArchived
                )

                SortDirection.DESC -> taskDao.getTaskEntitiesForParentOrderDesc(
                    parentTaskId = parentTaskId,
                    showCompleted = showCompleted,
                    showArchived = showArchived
                )
            }
        return entitiesFlow.map { it.toModels() }
    }

    override fun getUpcomingTasksForAlarm(dateInEpochDays: Int): Flow<List<TaskModel>> {
        return taskDao
            .getUpcomingTasksForAlarm(
                dateInEpochDays
            ).map { it.toModels() }
    }

}