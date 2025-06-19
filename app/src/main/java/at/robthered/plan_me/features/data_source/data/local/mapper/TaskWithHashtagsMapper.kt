package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.data.local.entities.TaskWithHashtagsRelation
import at.robthered.plan_me.features.data_source.domain.model.TaskWithHashtagsModel

fun TaskWithHashtagsRelation.toTaskWithHashtagsModel(): TaskWithHashtagsModel {
    return TaskWithHashtagsModel(
        task = task.toModel(),
        hashtags = hashtags.toModels(),
    )
}