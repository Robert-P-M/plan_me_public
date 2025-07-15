package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagNameHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HashtagNameHistoryDao {

    @Insert
    suspend fun insert(hashtagNameHistoryEntity: HashtagNameHistoryEntity): Long

    @Query("SELECT * FROM hashtag_name_history WHERE hashtag_name_history_id = :hashtagNameHistoryId")
    fun get(hashtagNameHistoryId: Long): Flow<HashtagNameHistoryEntity?>

    @Query("SELECT * FROM hashtag_name_history WHERE hashtag_id = :hashtagId ORDER BY created_at ASC")
    fun getForHashtag(hashtagId: Long): Flow<List<HashtagNameHistoryEntity>>

    @Query("DELETE FROM hashtag_name_history WHERE hashtag_name_history_id = :hashtagNameHistoryId")
    suspend fun delete(hashtagNameHistoryId: Long): Int
}