package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.move_task.MoveTaskLoadingStatus

fun MoveTaskLoadingStatus.toUiText(): UiText {
    return when (this) {
        MoveTaskLoadingStatus.MOVING_TASK -> UiText
            .StringResource(
                id = R.string.move_task_loading_status_starting
            )
    }
}