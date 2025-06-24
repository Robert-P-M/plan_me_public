package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskDescriptionHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskDescriptionHistoryRepository {

    suspend fun insert(taskDescriptionHistoryModel: TaskDescriptionHistoryModel): Long

    fun get(taskDescriptionHistoryId: Long): Flow<TaskDescriptionHistoryModel?>

    fun getForTask(taskId: Long): Flow<List<TaskDescriptionHistoryModel>>

    suspend fun delete(taskDescriptionHistoryId: Long): Int

}