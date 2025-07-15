package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskCompletedHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskCompletedHistoryRepository {

    suspend fun insert(taskCompletedHistoryModel: TaskCompletedHistoryModel): Long

    fun get(taskCompletedHistoryId: Long): Flow<TaskCompletedHistoryModel?>

    fun getForTask(taskId: Long): Flow<List<TaskCompletedHistoryModel>>

    suspend fun delete(taskCompletedHistoryId: Long): Int

}