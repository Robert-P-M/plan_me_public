package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_archive.ToggleArchiveTaskLoadingStatus

fun ToggleArchiveTaskLoadingStatus.toUiText(
    title: String,
): UiText {
    return when (this) {
        ToggleArchiveTaskLoadingStatus.SWITCH_TO_ARCHIVED -> UiText
            .StringResource(
                id = R.string.archive_task_loading_switch_to_archived,
                args = listOf(title)
            )

        ToggleArchiveTaskLoadingStatus.SWITCH_TO_UN_ARCHIVED -> UiText
            .StringResource(
                id = R.string.archive_task_loading_switch_to_unarchived,
                args = listOf(title)
            )
    }
}