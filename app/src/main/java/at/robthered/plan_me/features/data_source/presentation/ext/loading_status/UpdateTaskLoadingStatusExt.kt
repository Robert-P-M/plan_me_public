package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.update_task.UpdateTaskLoadingStatus

fun UpdateTaskLoadingStatus.toUiText(): UiText {
    return when (this) {
        UpdateTaskLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.update_task_loading_status_starting
            )
    }
}