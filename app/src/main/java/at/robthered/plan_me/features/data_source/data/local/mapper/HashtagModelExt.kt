package at.robthered.plan_me.features.data_source.data.local.mapper

import at.robthered.plan_me.features.data_source.domain.local.models.HashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel

fun HashtagModel.toUpdateHashtagModel(): UpdateHashtagModel {
    return UpdateHashtagModel(
        hashtagId = hashtagId,
        name = name
    )
}