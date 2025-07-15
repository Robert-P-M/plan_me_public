package at.robthered.plan_me.features.hashtag_picker_dialog.presentation.hashtag_picker_event

import at.robthered.plan_me.features.data_source.domain.model.hashtag.UiHashtagModel

sealed class HashtagPickerEvent {
    data class NewHashtags(val hashtags: List<UiHashtagModel> = emptyList()) : HashtagPickerEvent()
    data class CurrentHashtags(val hashtags: List<UiHashtagModel> = emptyList()) :
        HashtagPickerEvent()

    data object ClearEvent : HashtagPickerEvent()
}