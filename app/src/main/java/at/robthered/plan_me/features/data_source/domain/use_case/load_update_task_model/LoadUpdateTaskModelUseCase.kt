package at.robthered.plan_me.features.data_source.domain.use_case.load_update_task_model

import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import kotlinx.coroutines.flow.Flow

interface LoadUpdateTaskModelUseCase {
    suspend operator fun invoke(taskId: Long): Flow<UpdateTaskModel>
}