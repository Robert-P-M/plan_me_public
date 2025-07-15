package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.TaskPriorityHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskPriorityHistoryDao {
    @Insert
    suspend fun insert(taskPriorityHistoryEntity: TaskPriorityHistoryEntity): Long

    @Query("SELECT * FROM task_priority_history WHERE task_priority_history_id = :taskPriorityHistoryId")
    fun get(taskPriorityHistoryId: Long): Flow<TaskPriorityHistoryEntity?>

    @Query("SELECT * FROM task_priority_history WHERE task_id = :taskId ORDER BY created_at ASC")
    fun getForTask(taskId: Long): Flow<List<TaskPriorityHistoryEntity>>

    @Query("DELETE FROM task_priority_history WHERE task_priority_history_id = :taskPriorityHistoryId")
    suspend fun delete(taskPriorityHistoryId: Long): Int
}