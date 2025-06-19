package at.robthered.plan_me.features.data_source.domain.validation.update_hashtag_name

import at.robthered.plan_me.features.common.domain.getErrorOrNull
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError
import at.robthered.plan_me.features.data_source.domain.validation.HashtagNameValidator

class UpdateHashtagModelValidatorImpl(
    private val hashtagNameValidator: HashtagNameValidator,
) : UpdateHashtagModelValidator {
    override operator fun invoke(updateHashtagModel: UpdateHashtagModel): UpdateHashtagModelError {
        return UpdateHashtagModelError(
            name = hashtagNameValidator(value = updateHashtagModel.name).getErrorOrNull()
        )
    }
}