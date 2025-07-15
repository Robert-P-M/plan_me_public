package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HashtagDao {

    @Insert
    suspend fun insert(hashtagEntity: HashtagEntity): Long

    @Update
    suspend fun update(hashtagEntity: HashtagEntity)

    @Insert
    suspend fun insert(hashtagEntities: List<HashtagEntity>): List<Long>

    @Query("SELECT * FROM hashtag WHERE hashtag_id = :hashtagId")
    fun get(hashtagId: Long): Flow<HashtagEntity?>

    @Query("SELECT * FROM hashtag ORDER BY created_at ASC")
    fun getAll(): Flow<List<HashtagEntity>>


    @Query(
        """
        SELECT h.* FROM hashtag h
        JOIN hashtag_fts h_fts ON h_fts.rowid = h.hashtag_id
        WHERE h_fts.name MATCH :query || '*' 
        ORDER BY h.name ASC
    """
    )
    fun getByName(query: String): Flow<List<HashtagEntity>>

    @Query(
        """
        SELECT hashtag.* FROM hashtag
        INNER JOIN task_hashtags_cross_ref 
        ON hashtag.hashtag_id = task_hashtags_cross_ref.hashtag_id
        WHERE task_hashtags_cross_ref.task_id = :taskId
        ORDER BY hashtag.name ASC
    """
    )
    fun getHashtagsForTask(taskId: Long): Flow<List<HashtagEntity>>


    @Query("DELETE FROM hashtag WHERE hashtag_id = :hashtagId")
    suspend fun delete(hashtagId: Long): Int

}