package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceSuccessMessage

fun DeleteTaskHashtagReferenceSuccessMessage.toUiText(): UiText {
    return when (this) {
        DeleteTaskHashtagReferenceSuccessMessage.TASK_HASHTAG_REFERENCE_SUCCESS -> UiText
            .StringResource(
                id = R.string.delete_task_hashtag_reference_success_message_success
            )
    }
}