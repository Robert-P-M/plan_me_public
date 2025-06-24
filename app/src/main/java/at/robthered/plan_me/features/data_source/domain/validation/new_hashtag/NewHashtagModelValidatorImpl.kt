package at.robthered.plan_me.features.data_source.domain.validation.new_hashtag

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError
import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidator

class NewHashtagModelValidatorImpl(
    private val hashtagNameValidator: HashtagNameValidator,
) : NewHashtagModelValidator {
    override operator fun invoke(newHashtagModel: UiHashtagModel.NewHashTagModel): NewHashtagModelError {
        return NewHashtagModelError(
            name = hashtagNameValidator(value = newHashtagModel.name).getErrorOrNull()
        )
    }
}