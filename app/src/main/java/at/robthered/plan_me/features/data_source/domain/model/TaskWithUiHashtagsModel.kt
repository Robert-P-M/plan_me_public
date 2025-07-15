package at.robthered.plan_me.features.data_source.domain.model

import at.robthered.plan_me.features.data_source.domain.local.models.TaskModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

data class TaskWithUiHashtagsModel(
    val task: TaskModel,
    val hashtags: List<UiHashtagModel> = emptyList(),
)