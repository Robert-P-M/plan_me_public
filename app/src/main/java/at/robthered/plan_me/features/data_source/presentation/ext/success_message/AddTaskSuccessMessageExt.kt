package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.add_task.AddTaskSuccessMessage

fun AddTaskSuccessMessage.toUiText(): UiText {
    return when (this) {
        AddTaskSuccessMessage.TASK_SAVED -> UiText
            .StringResource(
                id = R.string.add_task_message_task_saved
            )
    }
}