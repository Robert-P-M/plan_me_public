package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.robthered.plan_me.features.data_source.data.local.entities.TaskScheduleEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskScheduleEventDao {

    @Insert
    suspend fun insert(taskScheduleEventEntity: TaskScheduleEventEntity): Long

    @Query("DELETE FROM task_schedule_event WHERE task_schedule_event_id =:taskScheduleEventId")
    suspend fun delete(taskScheduleEventId: Long): Int

    @Query("SELECT * FROM task_schedule_event WHERE task_id =:taskId ORDER BY created_at ASC")
    fun getForTask(taskId: Long): Flow<List<TaskScheduleEventEntity>>

}