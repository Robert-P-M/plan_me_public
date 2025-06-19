package at.robthered.plan_me.features.data_source.presentation.ext

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.domain.HashtagTasksDialogAppResourceError
import at.robthered.plan_me.features.common.presentation.UiText

fun HashtagTasksDialogAppResourceError.toUiText(): UiText {
    return when (this) {
        HashtagTasksDialogAppResourceError.HASHTAG_NOT_FOUND ->
            UiText
                .StringResource(
                    id = R.string.hashtag_tasks_dialog_app_resource_error_hashtag_not_found
                )
    }
}