package at.robthered.plan_me.features.data_source.data.repository

import at.robthered.plan_me.features.data_source.data.local.dao.HashtagWithTasksRelationDao
import at.robthered.plan_me.features.data_source.data.local.mapper.toModel
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalHashtagWithTasksRelationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalHashtagWithTasksRelationRepositoryImpl(
    private val hashtagWithTasksRelationDao: HashtagWithTasksRelationDao,
) : LocalHashtagWithTasksRelationRepository {


    override fun getHashtagWithTasks(hashtagId: Long): Flow<HashtagWithTasksModel?> {
        return hashtagWithTasksRelationDao
            .getHashtagWithTasks(hashtagId = hashtagId)
            .map { it?.toModel() }
    }
}