package at.robthered.plan_me.features.data_source.domain.local.models

data class HashtagWithTasksModel(
    val hashtag: HashtagModel,
    val tasks: List<TaskModel> = emptyList(),
)