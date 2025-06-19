package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagWithReferenceLoadingStatus

fun AddNewHashtagWithReferenceLoadingStatus.toUiText(): UiText {
    return when (this) {
        AddNewHashtagWithReferenceLoadingStatus.STARTING ->
            UiText
                .StringResource(
                    id = R.string.add_new_hashtag_with_reference_loading_status_starting
                )
    }
}