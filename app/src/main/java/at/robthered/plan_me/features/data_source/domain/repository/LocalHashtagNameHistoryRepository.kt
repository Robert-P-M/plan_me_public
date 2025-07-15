package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagNameHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalHashtagNameHistoryRepository {
    suspend fun insert(hashtagNameHistoryModel: HashtagNameHistoryModel): Long
    fun get(hashtagNameHistoryId: Long): Flow<HashtagNameHistoryModel?>
    fun getForHashtag(hashtagId: Long): Flow<List<HashtagNameHistoryModel>>
    suspend fun delete(hashtagNameHistoryId: Long): Int
}