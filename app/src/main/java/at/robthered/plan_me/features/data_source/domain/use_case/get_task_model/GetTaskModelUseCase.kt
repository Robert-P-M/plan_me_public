package at.robthered.plan_me.features.data_source.domain.use_case.get_task_model

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel

interface GetTaskModelUseCase {
    suspend operator fun invoke(taskId: Long): TaskModel?
}