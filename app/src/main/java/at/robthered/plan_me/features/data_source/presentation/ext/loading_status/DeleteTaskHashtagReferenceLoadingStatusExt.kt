package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_task_hashtag_reference.DeleteTaskHashtagReferenceLoadingStatus

fun DeleteTaskHashtagReferenceLoadingStatus.toUiText(): UiText {
    return when (this) {
        DeleteTaskHashtagReferenceLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.delete_task_hashtag_reference_loading_status_starting
            )
    }
}