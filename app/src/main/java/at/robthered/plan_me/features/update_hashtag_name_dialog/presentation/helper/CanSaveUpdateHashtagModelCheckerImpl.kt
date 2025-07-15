package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModelError

class CanSaveUpdateHashtagModelCheckerImpl : CanSaveUpdateHashtagModelChecker {
    override operator fun invoke(updateHashtagModelError: UpdateHashtagModelError): Boolean {
        return updateHashtagModelError.name == null
    }
}