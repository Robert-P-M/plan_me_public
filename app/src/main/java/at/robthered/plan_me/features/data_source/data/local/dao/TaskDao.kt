package at.robthered.plan_me.features.data_source.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import at.robthered.plan_me.features.data_source.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(taskEntity: TaskEntity): Long

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Upsert
    suspend fun upsert(taskEntity: TaskEntity)

    @Query("DELETE FROM task")
    suspend fun delete()

    @Query("DELETE FROM task WHERE task_id =:taskId")
    suspend fun delete(taskId: Long): Int

    @Query("DELETE FROM task WHERE task_id IN (:taskIds)")
    suspend fun delete(taskIds: List<Long>)

    @Query("SELECT * FROM task WHERE task_id =:taskId")
    fun getTaskEntityById(taskId: Long): Flow<TaskEntity?>

    @Query(
        value = """
            SELECT * FROM task
                WHERE section_id IS NULL
                AND parent_task_id IS NULL
                AND (:showCompleted IS NULL OR completed = :showCompleted)
                AND (:showArchived IS NULL OR archived = :showArchived)
                ORDER BY created_at DESC
            """
    )
    fun getRootTaskEntitiesOrderDesc(
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
    ): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT * FROM task
                WHERE section_id IS NULL
                AND parent_task_id IS NULL
                AND (:showCompleted IS NULL OR completed = :showCompleted)
                AND (:showArchived IS NULL OR archived = :showArchived)
                ORDER BY created_at ASC
            """
    )
    fun getRootTaskEntitiesOrderAsc(
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
    ): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT * FROM task
                WHERE section_id =:sectionId
                AND parent_task_id IS NULL  
                AND (:showCompleted IS NULL OR completed = :showCompleted)
                AND (:showArchived IS NULL OR archived = :showArchived)
                ORDER BY created_at DESC
            """
    )
    fun getTaskEntitiesForSectionOrderDesc(
        sectionId: Long,
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
    ): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT * FROM task
                WHERE section_id =:sectionId
                AND parent_task_id IS NULL  
                AND (:showCompleted IS NULL OR completed = :showCompleted)
                AND (:showArchived IS NULL OR archived = :showArchived)
                ORDER BY created_at ASC
            """
    )
    fun getTaskEntitiesForSectionOrderAsc(
        sectionId: Long,
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
    ): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT * FROM task 
                WHERE parent_task_id =:parentTaskId
                AND section_id IS NULL
                AND (:showCompleted IS NULL OR completed = :showCompleted)
                AND (:showArchived IS NULL OR archived = :showArchived)
                ORDER BY created_at DESC
            """
    )
    fun getTaskEntitiesForParentOrderDesc(
        parentTaskId: Long,
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
    ): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT * FROM task 
                WHERE parent_task_id =:parentTaskId
                AND section_id IS NULL
                AND (:showCompleted IS NULL OR completed = :showCompleted)
                AND (:showArchived IS NULL OR archived = :showArchived)
                ORDER BY created_at ASC
            """
    )
    fun getTaskEntitiesForParentOrderAsc(
        parentTaskId: Long,
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE current_task_schedule_event_is_notification_enabled = 1  AND current_task_schedule_event_start_date_in_epoch_days >= :dateInEpochDays")
    fun getUpcomingTasksForAlarm(
        dateInEpochDays: Int,
    ): Flow<List<TaskEntity>>

}