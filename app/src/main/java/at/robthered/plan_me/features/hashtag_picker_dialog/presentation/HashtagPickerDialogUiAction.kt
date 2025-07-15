package at.robthered.plan_me.features.hashtag_picker_dialog.presentation

import at.robthered.plan_me.features.data_source.domain.model.hashtag.FoundHashtagModel
import at.robthered.plan_me.features.data_source.domain.model.hashtag.NewHashtagModel

sealed interface HashtagPickerDialogUiAction {
    data object OnNavigateBack : HashtagPickerDialogUiAction
    data object OnResetState : HashtagPickerDialogUiAction
    data object OnSaveHashtags : HashtagPickerDialogUiAction
    data class OnChangeNewHashtagName(val name: String) : HashtagPickerDialogUiAction
    data class OnRemoveNewHashtag(val index: Int) : HashtagPickerDialogUiAction
    data class OnEditNewHashtagItem(val newHashtagModel: NewHashtagModel) :
        HashtagPickerDialogUiAction

    data class OnAddExistingHashtag(val foundHashtagModel: FoundHashtagModel) :
        HashtagPickerDialogUiAction

    data object OnSaveNewHashtag : HashtagPickerDialogUiAction
    data object OnResetNewHashtagModel : HashtagPickerDialogUiAction
}