package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import at.robthered.plan_me.features.data_source.data.local.entities.HashtagWithTasksRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface HashtagWithTasksRelationDao {
    @Transaction
    @Query("SELECT * FROM hashtag WHERE hashtag_id = :hashtagId")
    fun getHashtagWithTasks(hashtagId: Long): Flow<HashtagWithTasksRelation?>
}