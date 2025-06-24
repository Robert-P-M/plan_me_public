package at.robthered.plan_me.features.data_source.domain.model

data class TaskWithHashtagsAndChildrenModel(
    val taskWithUiHashtagsModel: TaskWithUiHashtagsModel,
    val children: List<TaskWithHashtagsAndChildrenModel> = emptyList(),
    val maxDepthReached: Boolean,
)