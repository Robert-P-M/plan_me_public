package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskArchivedHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskArchivedHistoryRepository {

    suspend fun insert(taskArchivedHistoryModel: TaskArchivedHistoryModel): Long

    fun get(taskArchivedHistoryId: Long): Flow<TaskArchivedHistoryModel?>

    fun getForTask(taskId: Long): Flow<List<TaskArchivedHistoryModel>>

    suspend fun delete(taskArchivedHistoryId: Long): Int

}