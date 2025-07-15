package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskSuccessMessage

fun UpdateTaskSuccessMessage.toUiText(): UiText {
    return when (this) {
        UpdateTaskSuccessMessage.TASK_UPDATED -> UiText
            .StringResource(
                id = R.string.update_task_success_message_task_updated
            )
    }
}