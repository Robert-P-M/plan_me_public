package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.helper

import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

class DidAddHashtagModelsChangeCheckerImpl : DidAddHashtagModelsChangeChecker {
    override operator fun invoke(
        initialList: List<UiHashtagModel>,
        currentList: List<UiHashtagModel>,
    ): Boolean {
        return initialList != currentList
    }
}