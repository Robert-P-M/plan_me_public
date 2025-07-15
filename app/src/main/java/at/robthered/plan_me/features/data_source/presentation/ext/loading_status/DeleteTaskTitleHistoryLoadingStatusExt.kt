package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_title_history.DeleteTaskTitleHistoryLoadingStatus

fun DeleteTaskTitleHistoryLoadingStatus.toUiText(): UiText {
    return when (this) {
        DeleteTaskTitleHistoryLoadingStatus.LOADING_DELETE_TASK_TITLE_HISTORY ->
            UiText
                .StringResource(
                    id = R.string.delete_task_title_history_loading
                )
    }
}