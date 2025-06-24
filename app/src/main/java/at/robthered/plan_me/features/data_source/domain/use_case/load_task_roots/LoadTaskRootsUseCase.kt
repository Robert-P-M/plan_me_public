package at.robthered.plan_me.features.data_source.domain.use_case.load_task_roots

import at.robthered.plan_me.features.data_source.domain.model.task_tree.TaskTreeModel
import kotlinx.coroutines.flow.Flow

interface LoadTaskRootsUseCase {
    operator fun invoke(taskId: Long): Flow<List<TaskTreeModel>>
}