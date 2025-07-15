package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.change_task_priority.ChangeTaskPriorityLoadingStatus

fun ChangeTaskPriorityLoadingStatus.toUiText(): UiText {
    return when (this) {
        ChangeTaskPriorityLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.change_task_priority_loading_status_starting
            )
    }
}