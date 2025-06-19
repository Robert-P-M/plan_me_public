package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskSuccessMessage

fun ToggleCompleteTaskSuccessMessage.toUiText(title: String): UiText {
    return when (this) {
        ToggleCompleteTaskSuccessMessage.TASK_COMPLETED -> UiText
            .StringResource(
                id = R.string.complete_task_message_task_completed,
                args = listOf(title)
            )

        ToggleCompleteTaskSuccessMessage.TASK_UN_COMPLETED -> UiText
            .StringResource(
                id = R.string.complete_task_message_task_un_completed,
                args = listOf(title)
            )
    }
}