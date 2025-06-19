package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskPriorityHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskPriorityHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskPriorityHistoryRepositoryImpl(
    private val taskPriorityHistoryDao: TaskPriorityHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskPriorityHistoryRepository {
    override suspend fun insert(taskPriorityHistoryModel: TaskPriorityHistoryModel): Long {
        return safeDatabaseExecutor {
            taskPriorityHistoryDao
                .insert(
                    taskPriorityHistoryEntity = taskPriorityHistoryModel.toEntity()
                )
        }
    }

    override fun get(taskPriorityHistoryId: Long): Flow<TaskPriorityHistoryModel?> {
        return taskPriorityHistoryDao
            .get(
                taskPriorityHistoryId = taskPriorityHistoryId
            )
            .map { it?.toModel() }
    }

    override fun getForTask(taskId: Long): Flow<List<TaskPriorityHistoryModel>> {
        return taskPriorityHistoryDao
            .getForTask(
                taskId = taskId
            )
            .map {
                it.toModels()
            }
    }

    override suspend fun delete(taskPriorityHistoryId: Long): Int {
        return safeDatabaseExecutor {
            taskPriorityHistoryDao
                .delete(
                    taskPriorityHistoryId = taskPriorityHistoryId
                )
        }
    }
}