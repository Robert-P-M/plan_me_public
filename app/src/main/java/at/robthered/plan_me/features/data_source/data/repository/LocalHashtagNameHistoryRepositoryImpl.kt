package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.HashtagNameHistoryDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toEntity
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.data.local.mapper.toModels
import at.robthered.plan_me.features.data_source.domain.local.executor.SafeDatabaseExecutor
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagNameHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalHashtagNameHistoryRepositoryImpl(
    private val hashtagNameHistoryDao: HashtagNameHistoryDao,
    private val safeDatabaseExecutor: SafeDatabaseExecutor,
) : LocalHashtagNameHistoryRepository {
    override suspend fun insert(hashtagNameHistoryModel: HashtagNameHistoryModel): Long {
        return safeDatabaseExecutor {
            hashtagNameHistoryDao.insert(hashtagNameHistoryEntity = hashtagNameHistoryModel.toEntity())
        }
    }

    override fun get(hashtagNameHistoryId: Long): Flow<HashtagNameHistoryModel?> {
        return hashtagNameHistoryDao.get(hashtagNameHistoryId = hashtagNameHistoryId)
            .map { it?.toModel() }
    }

    override fun getForHashtag(hashtagId: Long): Flow<List<HashtagNameHistoryModel>> {
        return hashtagNameHistoryDao.getForHashtag(hashtagId = hashtagId).map { it.toModels() }
    }

    override suspend fun delete(hashtagNameHistoryId: Long): Int {
        return safeDatabaseExecutor {
            hashtagNameHistoryDao.delete(hashtagNameHistoryId = hashtagNameHistoryId)
        }
    }
}