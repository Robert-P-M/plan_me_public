package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import kotlinx.coroutines.flow.Flow

interface LocalHashtagRepository {

    suspend fun insert(hashtagModel: HashtagModel): Long
    suspend fun update(hashtagModel: HashtagModel)

    suspend fun insert(hashtagModels: List<HashtagModel>): List<Long>

    fun get(hashtagId: Long): Flow<HashtagModel?>

    fun getByName(query: String): Flow<List<HashtagModel>>

    fun getHashtagsForTask(taskId: Long): Flow<List<HashtagModel>>

    fun getAll(): Flow<List<HashtagModel>>

    suspend fun delete(hashtagId: Long): Int

}