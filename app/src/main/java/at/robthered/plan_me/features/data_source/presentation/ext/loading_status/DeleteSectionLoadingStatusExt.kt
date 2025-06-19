package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.delete_section.DeleteSectionLoadingStatus

fun DeleteSectionLoadingStatus.toUiText(): UiText {
    return when (this) {
        DeleteSectionLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.delete_section_loading_status_starting
            )
    }
}