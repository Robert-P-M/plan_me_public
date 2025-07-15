package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskArchivedHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskArchivedHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskArchivedHistoryRepositoryImpl(
    private val taskArchivedHistoryDao: TaskArchivedHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskArchivedHistoryRepository {
    override suspend fun insert(taskArchivedHistoryModel: TaskArchivedHistoryModel): Long {
        return safeDatabaseExecutor {
            taskArchivedHistoryDao
                .insert(
                    taskArchivedHistoryEntity = taskArchivedHistoryModel.toEntity()
                )
        }
    }

    override fun get(taskArchivedHistoryId: Long): Flow<TaskArchivedHistoryModel?> {
        return taskArchivedHistoryDao
            .get(
                taskArchivedHistoryId = taskArchivedHistoryId,
            ).map {
                it?.toModel()
            }
    }

    override fun getForTask(taskId: Long): Flow<List<TaskArchivedHistoryModel>> {
        return taskArchivedHistoryDao
            .getForTask(
                taskId = taskId
            )
            .map {
                it.toModels()
            }
    }

    override suspend fun delete(taskArchivedHistoryId: Long): Int {
        return safeDatabaseExecutor {
            taskArchivedHistoryDao
                .delete(
                    taskArchivedHistoryId = taskArchivedHistoryId
                )
        }
    }
}