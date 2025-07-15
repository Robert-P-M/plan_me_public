package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_existing_hashtag.AddExistingHashtagReferenceSuccessMessage

fun AddExistingHashtagReferenceSuccessMessage.toUiText(): UiText {
    return when (this) {
        AddExistingHashtagReferenceSuccessMessage.REFERENCE_BETWEEN_HASHTAG_AND_TASK_CREATED ->
            UiText
                .StringResource(
                    id = R.string.add_existing_hashtag_reference_success_message_reference_between_hashtag_and_task_created
                )
    }
}