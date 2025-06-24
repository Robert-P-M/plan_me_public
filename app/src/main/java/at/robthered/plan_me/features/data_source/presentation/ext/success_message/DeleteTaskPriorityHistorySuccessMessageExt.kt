package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_priority_history.DeleteTaskPriorityHistorySuccessMessage

fun DeleteTaskPriorityHistorySuccessMessage.toUiText(): UiText {
    return when (this) {
        DeleteTaskPriorityHistorySuccessMessage.DELETE_TASK_PRIORITY_HISTORY_COMPLETED -> {
            UiText.StringResource(
                id = R.string.success_message_task_priority_history
            )
        }
    }
}