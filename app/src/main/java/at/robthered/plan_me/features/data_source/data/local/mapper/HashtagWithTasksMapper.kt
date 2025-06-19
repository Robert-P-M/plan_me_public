package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.data.local.entities.HashtagWithTasksRelation
import at.robthered.plan_me.features.data_source.domain.local.models.HashtagWithTasksModel

fun HashtagWithTasksRelation.toModel(): HashtagWithTasksModel {
    return HashtagWithTasksModel(
        hashtag = hashtag.toModel(),
        tasks = tasks.toModels()
    )
}

fun List<HashtagWithTasksRelation>.toModels(): List<HashtagWithTasksModel> {
    return this.map { it.toModel() }
}