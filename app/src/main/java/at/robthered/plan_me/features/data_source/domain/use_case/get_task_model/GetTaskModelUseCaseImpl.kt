package at.robthered.plan_me.features.data_source.domain.use_case.get_task_model

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.flow.first

class GetTaskModelUseCaseImpl(
    private val localTaskRepository: LocalTaskRepository,
) : GetTaskModelUseCase {
    override suspend operator fun invoke(taskId: Long): TaskModel? {
        return localTaskRepository.getTaskModelById(taskId = taskId).first()
    }
}