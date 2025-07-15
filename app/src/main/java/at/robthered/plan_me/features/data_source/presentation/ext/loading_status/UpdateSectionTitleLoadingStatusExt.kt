package at.robthered.plan_me.features.data_source.presentation.ext.loading_status

import at.robthered.plan_me.R
import at.robthered.plan_me.features.common.presentation.UiText
import at.robthered.plan_me.features.data_source.domain.use_case.update_section_title.UpdateSectionTitleLoadingStatus

fun UpdateSectionTitleLoadingStatus.toUiText(): UiText {
    return when (this) {
        UpdateSectionTitleLoadingStatus.STARTING -> UiText
            .StringResource(
                id = R.string.update_section_title_loading_starting
            )
    }
}