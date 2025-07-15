package at.robthered.plan_me.features.data_source.domain.repository

import at.robthered.plan_me.features.data_source.domain.local.models.TaskScheduleEventModel
import kotlinx.coroutines.flow.Flow

interface LocalTaskScheduleEventRepository {

    suspend fun insert(taskScheduleEventModel: TaskScheduleEventModel): Long

    suspend fun delete(taskScheduleEventId: Long): Int

    fun getForTask(taskId: Long): Flow<List<TaskScheduleEventModel>>

}