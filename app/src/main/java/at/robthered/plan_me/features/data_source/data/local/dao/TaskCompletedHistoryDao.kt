package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.TaskCompletedHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCompletedHistoryDao {

    @Insert
    suspend fun insert(taskCompletedHistoryEntity: TaskCompletedHistoryEntity): Long

    @Query("SELECT * FROM task_completed_history WHERE task_completed_history_id =:taskCompletedHistoryId")
    fun get(taskCompletedHistoryId: Long): Flow<TaskCompletedHistoryEntity?>

    @Query("SELECT * FROM task_completed_history WHERE task_id =:taskId ORDER BY created_at ASC")
    fun getForTask(taskId: Long): Flow<List<TaskCompletedHistoryEntity>>

    @Query("DELETE FROM task_completed_history WHERE task_completed_history_id =:taskCompletedHistoryId")
    suspend fun delete(taskCompletedHistoryId: Long): Int

}