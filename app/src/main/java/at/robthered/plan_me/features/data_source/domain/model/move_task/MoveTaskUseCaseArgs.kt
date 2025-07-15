package at.robthered.plan_me.features.data_source.domain.model.move_task

data class MoveTaskUseCaseArgs(
    val taskId: Long,
    val sectionId: Long?,
    val parentTaskId: Long?,
)