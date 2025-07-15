package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagReferenceLoadingStatus

fun AddExistingHashtagReferenceLoadingStatus.toUiText(): UiText {
    return when (this) {
        AddExistingHashtagReferenceLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.add_existing_hashtag_reference_loading_status_starting
            )
    }
}