package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.TaskHashtagsCrossRefDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntities
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.data.local.mapper.toTaskWithHashtagsModel
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskHashtagsCrossRefRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskHashtagsCrossRefRepositoryImpl(
    private val taskHashtagsCrossRefDao: TaskHashtagsCrossRefDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalTaskHashtagsCrossRefRepository {
    override suspend fun insert(crossRef: TaskHashtagsCrossRefModel): Long {
        return safeDatabaseExecutor {
            taskHashtagsCrossRefDao
                .insert(
                    crossRef = crossRef.toEntity()
                )
        }
    }

    override suspend fun insert(crossRefs: List<TaskHashtagsCrossRefModel>): List<Long> {
        return safeDatabaseExecutor {
            taskHashtagsCrossRefDao
                .insert(
                    crossRefs = crossRefs.toEntities()
                )
        }
    }


    override suspend fun deleteCrossRef(taskId: Long, hashtagId: Long): Int {
        return safeDatabaseExecutor {
            taskHashtagsCrossRefDao
                .deleteCrossRef(
                    taskId = taskId,
                    hashtagId = hashtagId
                )
        }
    }


    override fun getForTaskId(taskId: Long): Flow<TaskWithHashtagsModel?> {
        return taskHashtagsCrossRefDao
            .getForTaskId(taskId = taskId)
            .map { it?.toTaskWithHashtagsModel() }
    }


    override fun getCrossRefForTask(taskId: Long): Flow<List<TaskHashtagsCrossRefModel>> {
        return taskHashtagsCrossRefDao
            .getCrossRefForTask(taskId = taskId)
            .map { it.toModels() }
    }
}