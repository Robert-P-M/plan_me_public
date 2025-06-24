package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskTitleHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskTitleHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskTitleHistoryRepositoryImpl(
    private val taskTitleHistoryDao: TaskTitleHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskTitleHistoryRepository {
    override suspend fun insert(taskTitleHistoryModel: TaskTitleHistoryModel): Long {
        return safeDatabaseExecutor {
            taskTitleHistoryDao
                .insert(
                    taskTitleHistoryEntity = taskTitleHistoryModel.toEntity()
                )
        }
    }

    override fun get(taskTitleHistoryId: Long): Flow<TaskTitleHistoryModel?> {
        return taskTitleHistoryDao
            .get(
                taskTitleHistoryId = taskTitleHistoryId
            )
            .map {
                it?.toModel()
            }
    }

    override fun getForTask(taskId: Long): Flow<List<TaskTitleHistoryModel>> {
        return taskTitleHistoryDao
            .getForTask(
                taskId = taskId
            ).map {
                it.toModels()
            }
    }

    override suspend fun delete(taskTitleHistoryId: Long): Int {
        return safeDatabaseExecutor {
            taskTitleHistoryDao
                .delete(
                    taskTitleHistoryId = taskTitleHistoryId
                )
        }
    }
}