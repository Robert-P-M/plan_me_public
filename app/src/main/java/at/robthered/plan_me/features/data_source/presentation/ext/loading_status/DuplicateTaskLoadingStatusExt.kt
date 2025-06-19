package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.duplicate_task.DuplicateTaskLoadingStatus

fun DuplicateTaskLoadingStatus.toUiText(): UiText {
    return when (this) {
        DuplicateTaskLoadingStatus.DUPLICATING -> UiText
            .StringResource(
                id = R.string.duplicate_task_loading_duplicating
            )
    }
}