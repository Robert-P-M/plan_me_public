package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_description_history.DeleteTaskDescriptionHistoryLoadingStatus

fun DeleteTaskDescriptionHistoryLoadingStatus.toUiText(): UiText {
    return when (this) {
        DeleteTaskDescriptionHistoryLoadingStatus.LOADING_DELETE_TASK_DESCRIPTION_HISTORY ->
            UiText
                .StringResource(
                    id = R.string.delete_task_description_history_loading
                )
    }
}