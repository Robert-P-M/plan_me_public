package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameLoadingStatus

fun UpdateHashtagNameLoadingStatus.toUiText(): UiText {
    return when (this) {
        UpdateHashtagNameLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.update_hashtag_name_loading_status_starting
            )
    }
}