package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.SortDirection
import kotlinx.coroutines.flow.Flow

interface LocalTaskRepository {

    suspend fun insert(taskModel: TaskModel): Long

    suspend fun update(taskModel: TaskModel)

    suspend fun upsert(taskModel: TaskModel)

    suspend fun delete()

    suspend fun delete(taskId: Long): Int

    suspend fun delete(taskIds: List<Long>)

    fun getTaskModelById(taskId: Long): Flow<TaskModel?>
    fun getRootTaskModels(
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
        sortDirection: SortDirection = SortDirection.DESC,
    ): Flow<List<TaskModel>>

    fun getTaskModelsForSection(
        sectionId: Long,
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
        sortDirection: SortDirection = SortDirection.DESC,
    ): Flow<List<TaskModel>>

    fun getTaskModelsForParent(
        parentTaskId: Long,
        showCompleted: Boolean? = null,
        showArchived: Boolean? = null,
        sortDirection: SortDirection = SortDirection.DESC,
    ): Flow<List<TaskModel>>

    fun getUpcomingTasksForAlarm(
        dateInEpochDays: Int,
    ): Flow<List<TaskModel>>

}