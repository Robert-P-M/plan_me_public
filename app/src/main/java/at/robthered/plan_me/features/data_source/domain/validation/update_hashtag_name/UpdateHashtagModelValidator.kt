package at.robthered.plan_me.features.data_source.domain.validation.update_hashtag_name

import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError

interface UpdateHashtagModelValidator {
    operator fun invoke(updateHashtagModel: UpdateHashtagModel): UpdateHashtagModelError
}