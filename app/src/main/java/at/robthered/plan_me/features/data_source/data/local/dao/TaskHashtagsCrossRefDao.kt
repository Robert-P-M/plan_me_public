package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import at.robthered.plan_me.features.data_source.data.local.entities.TaskHashtagsCrossRefEntity
import at.robthered.plan_me.features.data_source.data.local.entities.TaskWithHashtagsRelation
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskHashtagsCrossRefDao {

    @Insert
    suspend fun insert(crossRef: TaskHashtagsCrossRefEntity): Long

    @Insert
    suspend fun insert(crossRefs: List<TaskHashtagsCrossRefEntity>): List<Long>

    @Query("DELETE FROM task_hashtags_cross_ref WHERE task_id = :taskId AND hashtag_id = :hashtagId")
    suspend fun deleteCrossRef(taskId: Long, hashtagId: Long): Int


    @Transaction
    @Query("SELECT * FROM task WHERE task_id =:taskId")
    fun getForTaskId(taskId: Long): Flow<TaskWithHashtagsRelation?>


    @Query("SELECT * FROM task_hashtags_cross_ref WHERE task_id =:taskId ")
    fun getCrossRefForTask(taskId: Long): Flow<List<TaskHashtagsCrossRefEntity>>

}