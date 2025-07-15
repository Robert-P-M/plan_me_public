package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.TaskArchivedHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskArchivedHistoryDao {

    @Insert
    suspend fun insert(taskArchivedHistoryEntity: TaskArchivedHistoryEntity): Long

    @Query("SELECT * FROM task_archived_history WHERE task_archived_history_id =:taskArchivedHistoryId")
    fun get(taskArchivedHistoryId: Long): Flow<TaskArchivedHistoryEntity?>

    @Query("SELECT * FROM task_archived_history WHERE task_id =:taskId ORDER BY created_at ASC")
    fun getForTask(taskId: Long): Flow<List<TaskArchivedHistoryEntity>>

    @Query("DELETE FROM task_archived_history WHERE task_archived_history_id =:taskArchivedHistoryId")
    suspend fun delete(taskArchivedHistoryId: Long): Int

}