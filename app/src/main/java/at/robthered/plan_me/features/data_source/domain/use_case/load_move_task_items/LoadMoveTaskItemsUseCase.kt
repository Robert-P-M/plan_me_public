package at.robthered.plan_me.features.data_source.domain.use_case.load_move_task_items

import at.robthered.plan_me.features.data_source.domain.model.move_task.MoveTaskModel
import kotlinx.coroutines.flow.Flow

interface LoadMoveTaskItemsUseCase {
    operator fun invoke(depth: Int = 5): Flow<List<MoveTaskModel>>
}