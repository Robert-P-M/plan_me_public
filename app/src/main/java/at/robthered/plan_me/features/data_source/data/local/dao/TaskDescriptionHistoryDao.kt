package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.TaskDescriptionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDescriptionHistoryDao {
    @Insert
    suspend fun insert(taskDescriptionHistoryEntity: TaskDescriptionHistoryEntity): Long

    @Query("SELECT * FROM task_description_history WHERE task_description_history_id = :taskDescriptionHistoryId")
    fun get(taskDescriptionHistoryId: Long): Flow<TaskDescriptionHistoryEntity?>

    @Query("SELECT * FROM task_description_history WHERE task_id = :taskId ORDER BY created_at ASC")
    fun getForTask(taskId: Long): Flow<List<TaskDescriptionHistoryEntity>>

    @Query("DELETE FROM task_description_history WHERE task_description_history_id = :taskDescriptionHistoryId")
    suspend fun delete(taskDescriptionHistoryId: Long): Int
}