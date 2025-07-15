package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskHashtagsCrossRefModel
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskHashtagsCrossRefRepository {
    suspend fun insert(crossRef: TaskHashtagsCrossRefModel): Long
    suspend fun insert(crossRefs: List<TaskHashtagsCrossRefModel>): List<Long>
    suspend fun deleteCrossRef(taskId: Long, hashtagId: Long): Int
    fun getForTaskId(taskId: Long): Flow<TaskWithHashtagsModel?>
    fun getCrossRefForTask(taskId: Long): Flow<List<TaskHashtagsCrossRefModel>>
}