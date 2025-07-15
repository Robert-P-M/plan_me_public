package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.ExistingHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.FoundHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

fun HashtagModel.toFoundHashtagModel(): UiHashtagModel.FoundHashtagModel {
    return UiHashtagModel.FoundHashtagModel(
        hashtagId = hashtagId,
        name = name
    )
}

fun List<HashtagModel>.toFoundHashtagModels(): List<UiHashtagModel.FoundHashtagModel> {
    return this.map { it.toFoundHashtagModel() }
}

fun FoundHashtagModel.toExistingHashtagModel(): ExistingHashtagModel {
    return UiHashtagModel.ExistingHashtagModel(
        hashtagId = hashtagId,
        name = name
    )

}