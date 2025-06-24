package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskPriorityHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskPriorityHistoryRepository {

    suspend fun insert(taskPriorityHistoryModel: TaskPriorityHistoryModel): Long

    fun get(taskPriorityHistoryId: Long): Flow<TaskPriorityHistoryModel?>

    fun getForTask(taskId: Long): Flow<List<TaskPriorityHistoryModel>>

    suspend fun delete(taskPriorityHistoryId: Long): Int

}