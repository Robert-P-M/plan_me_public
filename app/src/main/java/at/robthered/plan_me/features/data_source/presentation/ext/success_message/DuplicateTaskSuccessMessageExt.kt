package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskSuccessMessage

fun DuplicateTaskSuccessMessage.toUiText(): UiText {
    return when (this) {
        DuplicateTaskSuccessMessage.COMPLETED -> UiText
            .StringResource(
                id = R.string.success_message_task_duplicated_completed
            )
    }
}