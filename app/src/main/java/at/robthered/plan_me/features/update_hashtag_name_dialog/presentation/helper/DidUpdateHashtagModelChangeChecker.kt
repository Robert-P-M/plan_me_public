package at.robthered.plan_me.features.update_hashtag_name_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.update_hashtag.UpdateHashtagModel

interface DidUpdateHashtagModelChangeChecker {
    operator fun invoke(
        initialModel: UpdateHashtagModel,
        currentModel: UpdateHashtagModel,
    ): Boolean
}