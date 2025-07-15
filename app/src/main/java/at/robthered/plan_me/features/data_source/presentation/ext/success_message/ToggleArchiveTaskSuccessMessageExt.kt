package at.robthered.plan_me.features.data_source.presentation.ext.success_message

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskSuccessMessage

fun ToggleArchiveTaskSuccessMessage.toUiText(
    title: String,
): UiText {
    return when (this) {
        ToggleArchiveTaskSuccessMessage.TASK_ARCHIVED -> UiText
            .StringResource(
                id = R.string.archive_task_success_message_task_archived,
                args = listOf(title)
            )

        ToggleArchiveTaskSuccessMessage.TASK_UN_ARCHIVED -> UiText
            .StringResource(
                id = R.string.archive_task_success_message_task_un_archived,
                args = listOf(title)
            )
    }
}