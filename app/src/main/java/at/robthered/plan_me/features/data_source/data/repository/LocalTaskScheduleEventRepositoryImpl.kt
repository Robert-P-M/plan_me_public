package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskScheduleEventDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskScheduleEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskScheduleEventRepositoryImpl(
    private val taskScheduleEventDao: TaskScheduleEventDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskScheduleEventRepository {
    override suspend fun insert(taskScheduleEventModel: TaskScheduleEventModel): Long {
        return safeDatabaseExecutor {
            taskScheduleEventDao.insert(taskScheduleEventEntity = taskScheduleEventModel.toEntity())
        }
    }

    override suspend fun delete(taskScheduleEventId: Long): Int {
        return safeDatabaseExecutor {
            taskScheduleEventDao.delete(taskScheduleEventId)
        }
    }

    override fun getForTask(taskId: Long): Flow<List<TaskScheduleEventModel>> {
        return taskScheduleEventDao.getForTask(taskId = taskId)
            .map { it.toModels() }
    }
}