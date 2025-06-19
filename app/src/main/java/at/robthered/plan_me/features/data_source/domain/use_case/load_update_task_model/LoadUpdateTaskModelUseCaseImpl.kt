package at.robthered.plan_me.features.data_source.domain.use_case.load_update_task_model

import at.robthered.plan_me.features.data_source.data.local.mapper.toUpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.model.update_task.UpdateTaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadUpdateTaskModelUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
) : LoadUpdateTaskModelUseCase {
    override suspend operator fun invoke(taskId: Long): Flow<UpdateTaskModel> {
        return localTaskRepository.getTaskModelById(taskId = taskId).map {
            it?.toUpdateTaskModel() ?: UpdateTaskModel()
        }
    }
}