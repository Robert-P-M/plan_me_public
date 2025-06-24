package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_completed_history.DeleteTaskCompletedHistoryLoadingStatus

fun DeleteTaskCompletedHistoryLoadingStatus.toUiText(): UiText {
    return when (this) {
        DeleteTaskCompletedHistoryLoadingStatus.LOADING_DELETE_TASK_COMPLETED_HISTORY ->
            UiText
                .StringResource(
                    id = R.string.delete_task_completed_history_loading
                )
    }
}