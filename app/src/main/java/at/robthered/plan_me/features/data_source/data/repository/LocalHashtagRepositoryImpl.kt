package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.HashtagDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalHashtagRepositoryImpl(
    private val hashtagDao: HashtagDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalHashtagRepository {
    override suspend fun insert(hashtagModel: HashtagModel): Long {
        return safeDatabaseExecutor {
            hashtagDao.insert(hashtagEntity = hashtagModel.toEntity())
        }
    }

    override suspend fun update(hashtagModel: HashtagModel) {
        return safeDatabaseExecutor {
            hashtagDao.update(hashtagEntity = hashtagModel.toEntity())
        }
    }

    override suspend fun insert(hashtagModels: List<HashtagModel>): List<Long> {
        return safeDatabaseExecutor {
            hashtagDao.insert(hashtagEntities = hashtagModels.map { it.toEntity() })
        }
    }

    override fun get(hashtagId: Long): Flow<HashtagModel?> {
        return hashtagDao.get(hashtagId = hashtagId).map { it?.toModel() }
    }

    override fun getByName(query: String): Flow<List<HashtagModel>> {
        return hashtagDao.getByName(query).map { it.toModels() }
    }

    override fun getHashtagsForTask(taskId: Long): Flow<List<HashtagModel>> {
        return hashtagDao.getHashtagsForTask(taskId = taskId).map { it.toModels() }
    }

    override fun getAll(): Flow<List<HashtagModel>> {
        return hashtagDao.getAll().map { it.toModels() }
    }

    override suspend fun delete(hashtagId: Long): Int {
        return hashtagDao.delete(hashtagId = hashtagId)
    }
}