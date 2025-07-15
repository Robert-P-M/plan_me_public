package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_new_hashtag.AddNewHashtagWithReferenceSuccessMessage

fun AddNewHashtagWithReferenceSuccessMessage.toUiText(): UiText {
    return when (this) {
        AddNewHashtagWithReferenceSuccessMessage.NEW_HASHTAG_AND_REFERENCE_TO_TASK_CREATED ->
            UiText
                .StringResource(
                    id = R.string.add_new_hashtag_with_reference_success_message_new_hashtag_and_reference_to_task_created
                )
    }
}