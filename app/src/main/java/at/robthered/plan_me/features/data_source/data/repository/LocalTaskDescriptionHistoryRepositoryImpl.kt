package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskDescriptionHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskDescriptionHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskDescriptionHistoryRepositoryImpl(
    private val taskDescriptionHistoryDao: TaskDescriptionHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskDescriptionHistoryRepository {
    override suspend fun insert(taskDescriptionHistoryModel: TaskDescriptionHistoryModel): Long {
        return safeDatabaseExecutor {
            taskDescriptionHistoryDao
                .insert(
                    taskDescriptionHistoryEntity = taskDescriptionHistoryModel.toEntity()
                )
        }
    }

    override fun get(taskDescriptionHistoryId: Long): Flow<TaskDescriptionHistoryModel?> {
        return taskDescriptionHistoryDao
            .get(
                taskDescriptionHistoryId = taskDescriptionHistoryId
            ).map {
                it?.toModel()
            }
    }

    override fun getForTask(taskId: Long): Flow<List<TaskDescriptionHistoryModel>> {
        return taskDescriptionHistoryDao
            .getForTask(
                taskId = taskId
            ).map {
                it.toModels()
            }
    }

    override suspend fun delete(taskDescriptionHistoryId: Long): Int {
        return safeDatabaseExecutor {
            taskDescriptionHistoryDao
                .delete(
                    taskDescriptionHistoryId = taskDescriptionHistoryId
                )
        }
    }
}