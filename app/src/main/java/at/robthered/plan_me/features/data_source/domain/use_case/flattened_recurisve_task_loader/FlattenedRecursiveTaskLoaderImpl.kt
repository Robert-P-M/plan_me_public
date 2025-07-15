package at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader

import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import at.robthered.plan_me.features.data_source.domain.repository.LocalTaskRepository
import kotlinx.coroutines.flow.first

class FlattenedRecursiveTaskLoaderImpl(
    private val localTaskRepository: LocalTaskRepository,
) : FlattenedRecursiveTaskLoader {
    override suspend operator fun invoke(
        parentTaskId: Long,
        currentVisualDepth: Int,
        maxRecursionLevels: Int,
    ): List<MoveTaskModel.Task> {
        if (maxRecursionLevels < 0) {
            return emptyList()
        }

        val directChildren = localTaskRepository
            .getTaskModelsForParent(parentTaskId = parentTaskId)
            .first()

        val resultList = mutableListOf<MoveTaskModel.Task>()
        directChildren.forEach { childTWCModel ->
            resultList.add(
                MoveTaskModel.Task(
                    title = childTWCModel.title,
                    taskId = childTWCModel.taskId,
                    depth = currentVisualDepth
                )
            )
            resultList.addAll(
                invoke(
                    parentTaskId = childTWCModel.taskId,
                    currentVisualDepth = currentVisualDepth + 1,
                    maxRecursionLevels = maxRecursionLevels - 1
                )
            )
        }
        return resultList
    }
}