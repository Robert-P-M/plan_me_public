package at.robthered.plan_me.features.data_source.domain.use_case.flattened_recurisve_task_loader

import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel

interface FlattenedRecursiveTaskLoader {
    suspend operator fun invoke(
        parentTaskId: Long,
        currentVisualDepth: Int,
        maxRecursionLevels: Int,
    ): List<MoveTaskModel.Task>
}