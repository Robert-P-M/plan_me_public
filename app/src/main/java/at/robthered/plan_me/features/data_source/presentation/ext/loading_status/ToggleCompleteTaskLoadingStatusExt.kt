package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.toggle_complete.ToggleCompleteTaskLoadingStatus

fun ToggleCompleteTaskLoadingStatus.toUiText(title: String): UiText {
    return when (this) {
        ToggleCompleteTaskLoadingStatus.SWITCH_TO_COMPLETED -> UiText
            .StringResource(
                id = R.string.complete_task_loading_switch_to_completed,
                args = listOf(title)
            )

        ToggleCompleteTaskLoadingStatus.SWITCH_TO_UNCOMPLETED -> UiText
            .StringResource(
                id = R.string.complete_task_loading_switch_to_uncompleted,
                args = listOf(title)
            )
    }
}