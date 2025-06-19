package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPrioritySuccessMessage

fun ChangeTaskPrioritySuccessMessage.toUiText(): UiText {
    return when (this) {
        ChangeTaskPrioritySuccessMessage.TASK_PRIORITY_CHANGED -> UiText
            .StringResource(
                id = R.string.change_task_priority_success_message_starting
            )
    }
}