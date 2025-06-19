package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.update_hashtag_name.UpdateHashtagNameSuccessMessage

fun UpdateHashtagNameSuccessMessage.toUiText(): UiText {
    return when (this) {
        UpdateHashtagNameSuccessMessage.HASHTAG_UPDATED -> UiText
            .StringResource(
                id = R.string.update_hashtag_name_success_message_hashtag_updated
            )
    }
}