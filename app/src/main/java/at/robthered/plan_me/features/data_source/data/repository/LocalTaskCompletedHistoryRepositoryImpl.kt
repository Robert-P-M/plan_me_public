package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskCompletedHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskCompletedHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskCompletedHistoryRepositoryImpl(
    private val taskCompletedHistoryDao: TaskCompletedHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskCompletedHistoryRepository {
    override suspend fun insert(taskCompletedHistoryModel: TaskCompletedHistoryModel): Long {
        return safeDatabaseExecutor {
            taskCompletedHistoryDao
                .insert(
                    taskCompletedHistoryEntity = taskCompletedHistoryModel.toEntity()
                )
        }
    }

    override fun get(taskCompletedHistoryId: Long): Flow<TaskCompletedHistoryModel?> {
        return taskCompletedHistoryDao
            .get(
                taskCompletedHistoryId = taskCompletedHistoryId
            )
            .map {
                it?.toModel()
            }


    }

    override fun getForTask(taskId: Long): Flow<List<TaskCompletedHistoryModel>> {
        return taskCompletedHistoryDao
            .getForTask(taskId = taskId)
            .map {
                it.toModels()
            }
    }

    override suspend fun delete(taskCompletedHistoryId: Long): Int {
        return safeDatabaseExecutor {
            taskCompletedHistoryDao
                .delete(
                    taskCompletedHistoryId = taskCompletedHistoryId
                )
        }
    }
}