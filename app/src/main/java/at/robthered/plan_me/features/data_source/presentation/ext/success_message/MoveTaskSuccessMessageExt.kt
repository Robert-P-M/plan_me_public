package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskSuccessMessage

fun MoveTaskSuccessMessage.toUiText(): UiText {
    return when (this) {
        MoveTaskSuccessMessage.TASK_MOVED -> UiText
            .StringResource(
                id = R.string.move_task_success_message_task_moved
            )
    }
}