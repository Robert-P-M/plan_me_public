package at.robthered.plan_me.features.data_source.domain.model

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel

data class TaskWithHashtagsModel(
    val task: TaskModel,
    val hashtags: List<HashtagModel> = emptyList(),
)