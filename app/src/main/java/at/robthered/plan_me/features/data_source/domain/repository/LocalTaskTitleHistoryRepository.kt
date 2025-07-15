package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskTitleHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskTitleHistoryRepository {

    suspend fun insert(taskTitleHistoryModel: TaskTitleHistoryModel): Long

    fun get(taskTitleHistoryId: Long): Flow<TaskTitleHistoryModel?>

    fun getForTask(taskId: Long): Flow<List<TaskTitleHistoryModel>>

    suspend fun delete(taskTitleHistoryId: Long): Int

}