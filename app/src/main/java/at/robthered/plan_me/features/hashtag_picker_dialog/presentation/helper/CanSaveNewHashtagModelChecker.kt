package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModelError

interface CanSaveNewHashtagModelChecker {
    operator fun invoke(newHashtagModelError: NewHashtagModelError): Boolean
}