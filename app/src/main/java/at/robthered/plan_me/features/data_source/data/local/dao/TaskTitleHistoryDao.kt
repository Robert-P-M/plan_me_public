package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.TaskTitleHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTitleHistoryDao {

    @Insert
    suspend fun insert(taskTitleHistoryEntity: TaskTitleHistoryEntity): Long

    @Query("SELECT * FROM task_title_history WHERE task_title_history_id =:taskTitleHistoryId")
    fun get(taskTitleHistoryId: Long): Flow<TaskTitleHistoryEntity?>

    @Query("SELECT * FROM task_title_history WHERE task_id =:taskId ORDER BY created_at ASC")
    fun getForTask(taskId: Long): Flow<List<TaskTitleHistoryEntity>>

    @Query("DELETE FROM task_title_history WHERE task_title_history_id =:taskTitleHistoryId")
    suspend fun delete(taskTitleHistoryId: Long): Int


}