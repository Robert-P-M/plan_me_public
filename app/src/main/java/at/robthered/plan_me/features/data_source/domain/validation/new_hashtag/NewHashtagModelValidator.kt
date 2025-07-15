package at.robthered.plan_me.features.data_source.domain.validation.new_hashtag

import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel.NewHashTagModel

interface NewHashtagModelValidator {
    operator fun invoke(newHashtagModel: NewHashTagModel): NewHashtagModelError
}